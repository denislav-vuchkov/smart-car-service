package com.smart.garage.repositories.contracts;

import com.smart.garage.models.Vehicle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VehicleRepository extends BaseCRUDRepository<Vehicle> {

    List<Vehicle> getAll(Optional<String> identifier, Optional<Set<Integer>> owner,
                         Optional<Set<String>> brand, Optional<Set<Integer>> year,
                         Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

    List<Vehicle> getAll(Optional<String> identifier, Optional<String> owner,
                         Optional<String> make, Optional<String> model, Optional<Set<Integer>> year,
                         Optional<String> sorting, Optional<String> order, boolean excludeDeleted);

}
