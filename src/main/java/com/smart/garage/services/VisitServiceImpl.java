package com.smart.garage.services;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.models.*;
import com.smart.garage.repositories.contracts.VisitRepository;
import com.smart.garage.services.contracts.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.smart.garage.utility.AuthenticationHelper.*;
import static com.smart.garage.utility.VisitDataExtractor.validateStartAndEndDatesChronological;

@Service
public class VisitServiceImpl implements VisitService {

    public static final String OWNER_MISMATCH = "Vehicle and owner mismatch.";
    public static final String VISIT_DELETED = "Vehicle has already been deleted.";
    public static final String VISIT_STATUS_ERROR = "Only visits with status 'Requested' can be accepted.";

    private final VisitRepository visitRepository;
    private final UserServiceImpl userService;
    private final VehicleService vehicleService;
    private final ServicesService servicesService;
    private final ServiceRecordService serviceRecordService;
    private final EmailService emailService;

    @Autowired
    public VisitServiceImpl(VisitRepository visitRepository, VehicleService vehicleService,
                            UserServiceImpl userService, ServicesService servicesService,
                            ServiceRecordService serviceRecordService, EmailService emailService) {
        this.visitRepository = visitRepository;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.servicesService = servicesService;
        this.serviceRecordService = serviceRecordService;
        this.emailService = emailService;
    }

    @Override
    public List<Visit> getAll(User requester,
                              Optional<Set<Integer>> owner, Optional<Set<Integer>> vehicle, Optional<String> status,
                              Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate,
                              Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {
        if (isCustomer(requester)) {
            validateIsAccessingOwnInformation(requester, owner);
        } else {
            validateUserIsEmployee(requester);
        }
        return visitRepository.getAll(owner, vehicle, status, startDate, endDate, sorting, order, excludeDeleted);
    }

    @Override
    public List<Visit> getAll(User requester, boolean excludeDeleted) {
        return getAll(requester, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), excludeDeleted);
    }

    @Override
    public Visit getById(User requester, int id) {
        Visit visit = visitRepository.getById(id);
        if (isCustomer(requester)) {
            validateIsAccessingOwnInformation(requester, visit);
        } else {
            validateUserIsEmployee(requester);
        }
        return visit;
    }

    @Override
    public Visit create(User requester, Visit visit, Set<Integer> serviceIDs) {
        if (isCustomer(requester)) {
            validateIsAccessingOwnInformation(requester, visit.getVehicle());
        } else {
            validateUserIsEmployee(requester);
        }
        validateVehicleAndOwnerMatch(requester, visit);
        validateStartAndEndDatesChronological(visit);
        visitRepository.create(visit);
        Set<ServiceRecord> newServices = mapServices(serviceIDs, visit.getId());
        newServices.forEach(serviceRecord -> serviceRecordService.create(requester, serviceRecord, visit));
        return visit;
    }

    @Override
    public Visit create(User requester, User user, Vehicle vehicle, Visit visit, Set<Integer> serviceIDs) {
        validateUserIsEmployee(requester);
        userService.verifyFieldIsUnique("username", user.getUsername());
        userService.verifyFieldIsUnique("email", user.getEmail());
        userService.verifyFieldIsUnique("phoneNumber", user.getPhoneNumber());
        vehicleService.verifyFieldIsUnique(requester, "license", vehicle.getLicense());
        vehicleService.verifyFieldIsUnique(requester, "VIN", vehicle.getVIN());
        validateStartAndEndDatesChronological(visit);
        visit.setUser(userService.create(requester, user));
        vehicle.setUser(user);
        visit.setVehicle(vehicleService.create(requester, vehicle));
        return create(requester, visit, serviceIDs);
    }

    @Override
    public void accept(User requester, int id) {
        validateUserIsEmployee(requester);
        Visit visit = visitRepository.getById(id);
        validateRequestedStatus(visit);
        visit.setStatus(StatusCode.NOT_STARTED.getStatus());
        visitRepository.update(visit);
        emailService.send(visit.getUser().getEmail(),
                emailService.buildVisitConfirmationEmail(visit.getVehicle(), visit.getStartDate()),
                "Smart Garage - Visit Confirmation", null, null);
    }

    @Override
    public Visit update(User requester, Visit visit, Set<Integer> serviceIDs) {
        validateUserIsEmployee(requester);
        validateVehicleAndOwnerMatch(requester, visit);
        validateStartAndEndDatesChronological(visit);
        visitRepository.update(visit);
        Set<ServiceRecord> existingServices = visitRepository.getById(visit.getId()).getServices();
        Set<ServiceRecord> updatedServices = mapServices(serviceIDs, visit.getId());
        if (!existingServices.equals(updatedServices)) {
            existingServices.forEach(s -> serviceRecordService.delete(requester, s.getId()));
            visit.getServices().clear();
            updatedServices.forEach(serviceRecord -> serviceRecordService.create(requester, serviceRecord, visit));
        }
        return visit;
    }

    @Override
    public void softDelete(User requester, int id) {
        validateUserIsEmployee(requester);
        Visit visit = visitRepository.getById(id);
        if (visit.isDeleted()) throw new DuplicateEntityException(VISIT_DELETED);
        if (visit.getStatus().equals(StatusCode.REQUESTED.getStatus()))
            visit.setStatus(StatusCode.DECLINED.getStatus());
        visit.setDeleted(true);
        visitRepository.update(visit);
    }

    @Override
    public List<VisitStatus> getStatus() {
        return visitRepository.getStatus();
    }

    private void validateVehicleAndOwnerMatch(User requester, Visit visit) {
        User owner = userService.getById(requester, visit.getUser().getId());
        Vehicle vehicle = vehicleService.getById(requester, visit.getVehicle().getId());
        if (vehicle.getUser().getId() != owner.getId()) throw new InvalidParameter(OWNER_MISMATCH);
    }

    private void validateRequestedStatus(Visit visit) {
        if (!visit.getStatus().equals(StatusCode.REQUESTED.getStatus()))
            throw new IllegalStateException(VISIT_STATUS_ERROR);
    }

    private Set<ServiceRecord> mapServices(Set<Integer> serviceIDs, int visitID) {
        return serviceIDs.stream().map(servicesService::getById).map(service -> new ServiceRecord(
                0, visitID, service.getId(), service.getName(), service.getPriceBGN()
        )).collect(Collectors.toSet());
    }
}