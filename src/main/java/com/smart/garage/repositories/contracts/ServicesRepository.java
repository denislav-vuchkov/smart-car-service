package com.smart.garage.repositories.contracts;

import com.smart.garage.models.Servicez;
import com.smart.garage.models.dtos.ServicesDTOIn;

import java.util.List;
import java.util.Optional;

public interface ServicesRepository extends BaseCRUDRepository<Servicez> {

    List<Servicez> getAll(Optional<String> name, Optional<Integer> priceMinimum, Optional<Integer> priceMaximum,
                                                  Optional<String> sortBy, Optional<String> sortOrder);

    Servicez getMostExpensiveService();

    Servicez getByName(String name);
}
