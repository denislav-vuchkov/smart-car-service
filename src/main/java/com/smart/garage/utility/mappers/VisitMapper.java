package com.smart.garage.utility.mappers;


import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.NewCustomerDTO;
import com.smart.garage.models.dtos.VisitDTO;
import com.smart.garage.services.contracts.VehicleService;
import com.smart.garage.services.contracts.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.smart.garage.utility.VisitDataExtractor.*;

@Component
public class VisitMapper {

    private final VisitService visitService;
    private final VehicleService vehicleService;

    @Autowired
    public VisitMapper(VisitService visitService, VehicleService vehicleService) {
        this.visitService = visitService;
        this.vehicleService = vehicleService;
    }

    public Visit toObject(User requester, VisitDTO visitDTO) {
        return toObject(requester, visitDTO, new Visit());
    }

    public Visit toObject(User requester, VisitDTO visitDTO, Visit visit) {
        Vehicle vehicle = vehicleService.getById(requester, visitDTO.getVehicleID());
        visit.setUser(vehicle.getUser());
        visit.setVehicle(vehicle);
        visit.setStatus(getStatusById(requester, visitDTO));
        visit.setStartDate(parseDate(visitDTO.getStartDate()));
        visit.setEndDate(parseEndDate(visitDTO));
        visit.setDeleted(false);
        return visit;
    }

    public Visit toObject(User requester, NewCustomerDTO dto) {
        VisitDTO visitDTO = new VisitDTO(0, dto.getStatusID(), dto.getStartDate(), dto.getEndDate(), dto.getServiceIDs());
        Visit visit = new Visit();
        visit.setStatus(getStatusById(requester, visitDTO));
        visit.setStartDate(parseDate(visitDTO.getStartDate()));
        visit.setEndDate(parseEndDate(visitDTO));
        visit.setDeleted(false);
        return visit;
    }

    public VisitDTO toDTO(Visit visit) {
        VisitDTO visitDTO = new VisitDTO();
        visitDTO.setVehicleID(visit.getVehicle().getId());
        visitDTO.setStatusID(visit.getStatus().getId());
        visitDTO.setStartDate(formatDate(visit.getStartDate()));
        visitDTO.setEndDate(formatEndDate(visit));
        visitDTO.setServiceIDs(extractServiceIDs(visit));
        return visitDTO;
    }

    private VisitStatus getStatusById(User requester, VisitDTO visitDTO) {
        return visitService.getStatus().stream()
                .filter(s -> s.getId() == visitDTO.getStatusID()).findAny()
                .orElseThrow(() -> new EntityNotFoundException("Invalid/non-existing status selected."));
    }

    private LinkedHashSet<Integer> extractServiceIDs(Visit visit) {
        return visit.getServices().stream()
                .sorted(Comparator.comparing(ServiceRecord::getServiceName))
                .map(ServiceRecord::getServiceID)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
