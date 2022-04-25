package com.smart.garage.services.contracts;

import com.smart.garage.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll(User requester);

    List<User> getAllFiltered(int roleId, User requester, Optional<String> username, Optional<String> email,
                              Optional<String> phoneNumber, Optional<String> licenseOrVIN, Optional<String> make,
                              Optional<String> model, Optional<String> sortBy, Optional<String> sortOrder);

    User getById(User requester, int id);

    User create(User requester, User user);

    User update(User requester, User user);

    User updateContactDetails(User requester, User user);

    void verifyFieldIsUnique(String field, String value);

    void updatePassword(User requester, int id, String password);

    void softDelete(User requester, int id);

    User getByField(String field, String value);

    void generateResetPasswordEmail(User user);

    void confirmToken(String token);
}
