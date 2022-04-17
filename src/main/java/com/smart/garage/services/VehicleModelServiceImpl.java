package com.smart.garage.services;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.models.User;
import com.smart.garage.models.VehicleModel;
import com.smart.garage.repositories.contracts.VehicleModelRepository;
import com.smart.garage.services.contracts.VehicleModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smart.garage.utility.AuthenticationHelper.validateUserIsEmployee;
import static com.smart.garage.utility.VehicleModelExtractor.INVALID_MODEL_ERROR;
import static com.smart.garage.utility.VehicleModelExtractor.validateNameLength;

@Service
public class VehicleModelServiceImpl implements VehicleModelService {

    private final VehicleModelRepository vehicleModelRepository;

    @Autowired
    public VehicleModelServiceImpl(VehicleModelRepository vehicleModelRepository) {
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public List<VehicleModel> getAll(User requester) {
        validateUserIsEmployee(requester);
        return vehicleModelRepository.getAll();
    }

    @Override
    public VehicleModel getById(User requester, int id) {
        validateUserIsEmployee(requester);
        return vehicleModelRepository.getById(id);
    }

    @Override
    public VehicleModel getByField(User requester, String field, String value) {
        validateUserIsEmployee(requester);
        return vehicleModelRepository.getByField(field, value);
    }

    @Override
    public VehicleModel create(User requester, VehicleModel vehicleModel) {
        validateUserIsEmployee(requester);
        if (vehicleModel.getMake() == null) throw new IllegalVehicleBrand(INVALID_MODEL_ERROR);
        validateNameLength(vehicleModel.getName());
        vehicleModelRepository.verifyFieldIsUnique("name", vehicleModel.getName());
        vehicleModelRepository.create(vehicleModel);
        return vehicleModel;
    }

    @Override
    public VehicleModel update(User requester, VehicleModel vehicleModel) {
        validateUserIsEmployee(requester);
        if (vehicleModel.getMake() == null) throw new IllegalVehicleBrand(INVALID_MODEL_ERROR);
        validateNameLength(vehicleModel.getName());
        vehicleModelRepository.verifyFieldIsUnique("name", vehicleModel.getName());
        vehicleModelRepository.update(vehicleModel);
        return vehicleModel;
    }

    @Override
    public void delete(User requester, int id) {
        validateUserIsEmployee(requester);
        VehicleModel vehicleModel = getById(requester, id);
        vehicleModelRepository.delete(vehicleModel.getId());
    }

    @Override
    public boolean nameExists(User requester, String name) {
        validateUserIsEmployee(requester);
        try {
            vehicleModelRepository.getByField("name", name);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
