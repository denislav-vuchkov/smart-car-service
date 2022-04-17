package com.smart.garage.repositories.contracts;

import com.smart.garage.models.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends BaseCRUDRepository<User> {

    List<User> getAllFiltered(int roleId, Optional<String> username, Optional<String> email, Optional<String> phoneNumber,
                              Optional<String> licenseOrVIN, Optional<String> make, Optional<String> model,
                              Optional<String> sortBy, Optional<String> sortOrder);

}
