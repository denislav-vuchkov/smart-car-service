package com.smart.garage.services;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.models.VehicleModel;
import com.smart.garage.repositories.contracts.VehicleRepository;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.services.contracts.VehicleMakeService;
import com.smart.garage.services.contracts.VehicleModelService;
import com.smart.garage.services.contracts.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.smart.garage.utility.AuthenticationHelper.*;

@Service
public class VehicleServiceImpl implements VehicleService {

    public static final String VEHICLE_DELETED = "Vehicle has already been deleted.";

    private final VehicleRepository vehicleRepository;
    private final VehicleMakeService vehicleMakeService;
    private final VehicleModelService vehicleModelService;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository,
                              VehicleMakeService vehicleMakeService,
                              VehicleModelService vehicleModelService, UserService userService) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMakeService = vehicleMakeService;
        this.vehicleModelService = vehicleModelService;
    }

    @Override
    public List<Vehicle> getAll(User requester, Optional<String> identifier, Optional<Set<Integer>> owner,
                                Optional<Set<String>> brand, Optional<Set<Integer>> year,
                                Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {
        if (isCustomer(requester)) {
            validateIsAccessingOwnInformation(requester, owner);
        } else {
            validateUserIsEmployee(requester);
        }
        return vehicleRepository.getAll(identifier, owner, brand, year, sorting, order, excludeDeleted);
    }

    @Override
    public List<Vehicle> getAll(User requester, Optional<String> identifier, Optional<String> owner,
                                Optional<String> make, Optional<String> model, Optional<Set<Integer>> year,
                                Optional<String> sorting, Optional<String> order, boolean excludeDeleted) {
        validateUserIsEmployee(requester);
        return vehicleRepository.getAll(identifier, owner, make, model, year, sorting, order, excludeDeleted);
    }

    @Override
    public List<Vehicle> getAll(User requester, boolean excludeDeleted) {
        return getAll(requester, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), excludeDeleted);
    }

    @Override
    public Vehicle getById(User requester, int id) {
       Vehicle vehicle = vehicleRepository.getById(id);
        if (isCustomer(requester)) {
            validateIsAccessingOwnInformation(requester, vehicle);
        } else {
            validateUserIsEmployee(requester);
        }
        return vehicle;
    }

    @Override
    public Vehicle create(User requester, Vehicle vehicle) {
        validateUserIsEmployee(requester);
        vehicleRepository.verifyFieldIsUnique("license", vehicle.getLicense());
        vehicleRepository.verifyFieldIsUnique("VIN", vehicle.getVIN());
        vehicle.setModel(getModel(requester, vehicle));
        vehicleRepository.create(vehicle);
        return vehicle;
    }

    @Override
    public Vehicle update(User requester, Vehicle vehicle) {
        validateUserIsEmployee(requester);
        Vehicle original = vehicleRepository.getById(vehicle.getId());
        verifyFieldIsUniqueIfChanged("license", original.getLicense(), vehicle.getLicense());
        verifyFieldIsUniqueIfChanged("VIN", original.getVIN(), vehicle.getVIN());
        vehicle.setModel(getModel(requester, vehicle));
        vehicleRepository.update(vehicle);
        return vehicle;
    }

    @Override
    public void softDelete(User requester, int id) {
        validateUserIsEmployee(requester);
        Vehicle vehicle = vehicleRepository.getById(id);
        if (vehicle.isDeleted()) throw new DuplicateEntityException(VEHICLE_DELETED);
        vehicle.setDeleted(true);
        vehicleRepository.update(vehicle);
    }

    @Override
    public void verifyFieldIsUnique(User requester, String field, String value) {
        validateUserIsEmployee(requester);
        vehicleRepository.verifyFieldIsUnique(field, value);
    }

    private void verifyFieldIsUniqueIfChanged(String field, String original, String update) {
        if (!original.equalsIgnoreCase(update)) vehicleRepository.verifyFieldIsUnique(field, update);
    }

    private VehicleModel getModel(User requester, Vehicle vehicle) {
        VehicleMake incomingMake = vehicle.getModel().getMake();
        VehicleModel incomingModel = vehicle.getModel();

        if (vehicleMakeService.nameExists(requester, incomingMake.getName())
                && vehicleModelService.nameExists(requester, incomingModel.getName())) {
            incomingModel = vehicleModelService.getByField(requester, "name", incomingModel.getName());

        } else if (vehicleMakeService.nameExists(requester, incomingMake.getName())
                && !vehicleModelService.nameExists(requester, incomingModel.getName())) {
            incomingMake = vehicleMakeService.getByField(requester, "name", incomingMake.getName());
            incomingModel.setMake(incomingMake);
            incomingModel = vehicleModelService.create(requester, incomingModel);

        } else {
            incomingMake = vehicleMakeService.create(requester, incomingMake);
            incomingModel.setMake(incomingMake);
            try {
                incomingModel = vehicleModelService.create(requester, incomingModel);
            } catch (IllegalVehicleBrand e) {
                vehicleMakeService.delete(requester, incomingMake.getId());
                throw new IllegalVehicleBrand(e.getMessage());
            }
        }
        return incomingModel;
    }
}
