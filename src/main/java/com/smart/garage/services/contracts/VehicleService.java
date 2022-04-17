package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VehicleService {

    List<Vehicle> getAll(User requester, Optional<String> identifier, Optional<Set<Integer>> owner,
                         Optional<Set<String>> brand, Optional<Set<Integer>> year,
                         Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

    List<Vehicle> getAll(User requester, Optional<String> identifier, Optional<String> owner,
                         Optional<String> make, Optional<String> model, Optional<Set<Integer>> year,
                         Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

    List<Vehicle> getAll(User requester, boolean excludeDeleted);

    Vehicle getById(User requester, int id);

    Vehicle create(User requester, Vehicle vehicle);

    Vehicle update(User requester, Vehicle vehicle);

    void softDelete(User requester, int id);

    void verifyFieldIsUnique(User requester, String field, String value);

}
