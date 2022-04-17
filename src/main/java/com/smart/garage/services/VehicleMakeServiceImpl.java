package com.smart.garage.services;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.User;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.repositories.contracts.VehicleMakeRepository;
import com.smart.garage.services.contracts.VehicleMakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.smart.garage.utility.AuthenticationHelper.validateUserIsEmployee;
import static com.smart.garage.utility.VehicleModelExtractor.validateNameLength;

@Service
public class VehicleMakeServiceImpl implements VehicleMakeService {

    private final VehicleMakeRepository vehicleMakeRepository;

    @Autowired
    public VehicleMakeServiceImpl(VehicleMakeRepository vehicleMakeRepository) {
        this.vehicleMakeRepository = vehicleMakeRepository;
    }

    @Override
    public List<VehicleMake> getAll(User requester) {
        validateUserIsEmployee(requester);
        return vehicleMakeRepository.getAll();
    }

    @Override
    public VehicleMake getById(User requester, int id) {
        validateUserIsEmployee(requester);
        return vehicleMakeRepository.getById(id);
    }

    @Override
    public VehicleMake getByField(User requester, String field, String value) {
        validateUserIsEmployee(requester);
        return vehicleMakeRepository.getByField(field, value);
    }

    @Override
    public VehicleMake create(User requester, VehicleMake vehicleMake) {
        validateUserIsEmployee(requester);
        validateNameLength(vehicleMake.getName());
        vehicleMakeRepository.verifyFieldIsUnique("name", vehicleMake.getName());
        vehicleMakeRepository.create(vehicleMake);
        return vehicleMake;
    }

    @Override
    public VehicleMake update(User requester, VehicleMake vehicleMake) {
        validateUserIsEmployee(requester);
        validateNameLength(vehicleMake.getName());
        vehicleMakeRepository.verifyFieldIsUnique("name", vehicleMake.getName());
        vehicleMakeRepository.update(vehicleMake);
        return vehicleMake;
    }

    @Override
    public void delete(User requester, int id) {
        validateUserIsEmployee(requester);
        VehicleMake vehicleMake = vehicleMakeRepository.getById(id);
        vehicleMakeRepository.delete(vehicleMake.getId());
    }

    @Override
    public boolean nameExists(User requester, String name) {
        validateUserIsEmployee(requester);
        try {
            vehicleMakeRepository.getByField("name", name);
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}
