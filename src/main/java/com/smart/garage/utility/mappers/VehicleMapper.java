package com.smart.garage.utility.mappers;


import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.models.VehicleModel;
import com.smart.garage.models.dtos.NewCustomerDTO;
import com.smart.garage.models.dtos.VehicleDTO;
import com.smart.garage.models.dtos.VehicleModelDTO;
import com.smart.garage.models.dtos.VehicleREST;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.services.contracts.VehicleMakeService;
import com.smart.garage.services.contracts.VehicleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.smart.garage.utility.VehicleModelExtractor.*;

@Component
public class VehicleMapper {

    private final VehicleMakeService vehicleMakeService;
    private final VehicleModelService vehicleModelService;
    private final UserService userService;

    @Autowired
    public VehicleMapper(VehicleMakeService vehicleMakeService,
                         VehicleModelService vehicleModelService,
                         UserService userService) {
        this.vehicleMakeService = vehicleMakeService;
        this.vehicleModelService = vehicleModelService;
        this.userService = userService;
    }

    public Vehicle toObject(User requester, VehicleDTO vehicleDTO) {
        return toObject(requester, vehicleDTO, new Vehicle());
    }

    public Vehicle toObject(User requester, VehicleREST vehicleREST) {
        return toObject(requester, vehicleREST, new Vehicle());
    }

    public Vehicle toObject(User requester, VehicleDTO vehicleDTO, Vehicle vehicle) {
        vehicle.setUser(userService.getById(requester, vehicleDTO.getOwnerID()));
        vehicle.setLicense(vehicleDTO.getLicense());
        vehicle.setVIN(vehicleDTO.getVIN());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setDeleted(false);
        setModel(vehicleDTO.getVehicleModelDTO(), vehicle, requester);
        return vehicle;
    }

    public Vehicle toObject(User requester, VehicleREST vehicleREST, Vehicle vehicle) {
        vehicle.setUser(userService.getById(requester, vehicleREST.getOwnerID()));
        vehicle.setLicense(vehicleREST.getLicense());
        vehicle.setVIN(vehicleREST.getVIN());
        vehicle.setYear(vehicleREST.getYear());
        vehicle.setDeleted(false);
        vehicle.setModel(new VehicleModel(0, vehicleREST.getModelName().trim(),
                new VehicleMake(0, vehicleREST.getMakeName().trim())));
        return vehicle;
    }

    public Vehicle toObject(User requester, NewCustomerDTO dto) {
        VehicleDTO vehicleDTO = new VehicleDTO(dto.getLicense(), dto.getVIN(), 0, dto.getYear(),
                dto.getVehicleModelDTO());
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense(vehicleDTO.getLicense());
        vehicle.setVIN(vehicleDTO.getVIN());
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setDeleted(false);
        setModel(vehicleDTO.getVehicleModelDTO(), vehicle, requester);
        return vehicle;
    }

    public VehicleDTO toDTO(Vehicle vehicle) {
        VehicleDTO vehicleDTO = new VehicleDTO();
        vehicleDTO.setLicense(vehicle.getLicense());
        vehicleDTO.setVIN(vehicle.getVIN());
        vehicleDTO.setOwnerID(vehicle.getUser().getId());
        vehicleDTO.setYear(vehicle.getYear());
        vehicleDTO.setVehicleModelDTO(new VehicleModelDTO(vehicle.getModel()));
        return vehicleDTO;
    }

    private void setModel(VehicleModelDTO modelDTO, Vehicle vehicle, User requester) {
        validateNonDualModelProvided(modelDTO);
        if (containsExistingModel(modelDTO)) {
            vehicle.setModel(vehicleModelService.getById(requester, modelDTO.getModelID()));
        } else if (containsValidHybridModel(modelDTO)) {
            extractHybridModel(requester, vehicle, modelDTO);
        } else if (containsValidVerbatimModel(modelDTO)) {
            extractVerbatimModel(requester, vehicle, modelDTO);
        } else {
            throw new IllegalVehicleBrand(INVALID_MODEL_ERROR);
        }
    }

    private void extractHybridModel(User requester, Vehicle vehicle, VehicleModelDTO modelDTO) {
        validateNameLength(modelDTO.getNewModel());
        validateModelIsNew(requester, modelDTO.getNewModel().trim());
        vehicle.setModel(new VehicleModel(0, modelDTO.getNewModel().trim(),
                vehicleMakeService.getById(requester, modelDTO.getMakeID())));
    }

    private void extractVerbatimModel(User requester, Vehicle vehicle, VehicleModelDTO modelDTO) {
        validateNameLength(modelDTO.getVerbatimMake());
        validateNameLength(modelDTO.getVerbatimModel());
        validateMakeIsNew(requester, modelDTO.getVerbatimMake().trim());
        validateModelIsNew(requester, modelDTO.getVerbatimModel().trim());
        vehicle.setModel(new VehicleModel(0, modelDTO.getVerbatimModel().trim(),
                new VehicleMake(0, modelDTO.getVerbatimMake().trim())));
    }

    private void validateMakeIsNew(User requester, String name) {
        if (vehicleMakeService.nameExists(requester, name)) throw new IllegalVehicleBrand(DUPLICATE_MODEL_NAME);
    }

    private void validateModelIsNew(User requester, String name) {
        if (vehicleModelService.nameExists(requester, name)) throw new IllegalVehicleBrand(DUPLICATE_MODEL_NAME);
    }
}
