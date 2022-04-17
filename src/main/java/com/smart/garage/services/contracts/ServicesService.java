package com.smart.garage.services.contracts;

import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.models.dtos.ServicesDTOIn;

import java.util.List;
import java.util.Optional;

public interface ServicesService {

    List<Servicez> getAll(Optional<String> name, Optional<Integer> priceMinimum, Optional<Integer> priceMaximum,
                          Optional<String> sortBy, Optional<String> sortOrder);

    Servicez getById(int id);

    Servicez create(User requester, Servicez service);

    Servicez update(User requester, Servicez service);

    void delete(User requester, int id);

    Servicez getMostExpensiveService();
}
