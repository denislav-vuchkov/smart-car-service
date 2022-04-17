package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import com.smart.garage.models.VehicleModel;

import java.util.List;

public interface VehicleModelService {

    List<VehicleModel> getAll(User requester);

    VehicleModel getById(User requester, int id);

    VehicleModel getByField(User requester, String field, String value);

    VehicleModel create(User requester, VehicleModel vehicleModel);

    VehicleModel update(User requester, VehicleModel vehicleModel);

    void delete(User requester, int id);

    boolean nameExists(User requester, String name);
}
