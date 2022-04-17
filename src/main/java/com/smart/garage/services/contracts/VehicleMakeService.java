package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import com.smart.garage.models.VehicleMake;

import java.util.List;

public interface VehicleMakeService {

    List<VehicleMake> getAll(User requester);

    VehicleMake getById(User requester, int id);

    VehicleMake getByField(User requester, String field, String value);

    VehicleMake create(User requester, VehicleMake vehicleMake);

    VehicleMake update(User requester, VehicleMake vehicleMake);

    void delete(User requester, int id);

    boolean nameExists(User requester, String name);
}
