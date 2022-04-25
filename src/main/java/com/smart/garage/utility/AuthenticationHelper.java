package com.smart.garage.utility;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.Visit;
import com.smart.garage.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class AuthenticationHelper {

    public static final String UNAUTHORISED_NOT_LOGGED = "You have to be logged in order to access the requested resource!";
    public static final String UNAUTHORISED_LOGGED = "You cannot access this resource while logged in! Please log out and try again.";

    public static final String RESTRICTED_FOR_CUSTOMERS = "This resource is restricted for customers only.";
    public static final String RESTRICTED_FOR_EMPLOYEES = "This resource is restricted for employees and admins only.";

    public static final String RESTRICTED_FOR_OWNER = "This resource is restricted for the owner of the account.";

    private final UserService userService;

    @Autowired
    public AuthenticationHelper(UserService userService) {
        this.userService = userService;
    }

    public static boolean isEmployee(User user) {
        return user != null && user.getRole().getId() == UserRoles.EMPLOYEE.getValue();
    }

    public static boolean isCustomer(User requester) {
        return requester.getRole().getId() == UserRoles.CUSTOMER.getValue();
    }

    public static void validateUserIsEmployee(User user) {
        if (!isEmployee(user)) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_EMPLOYEES);
        }
    }

    public static void validateUserIsCustomer(User user) {
        if (!isCustomer(user)) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_CUSTOMERS);
        }
    }

    public static void validateIsAccessingOwnInformation(User requester, Optional<Set<Integer>> owner) {
        if (owner.isEmpty() || (!(owner.get().size() == 1 && owner.get().contains(requester.getId())))) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER);
        }
    }

    public static void validateIsAccessingOwnInformation(User requester, Visit visit) {
        validateIsAccessingOwnInformation(requester, Optional.of(Set.of(visit.getUser().getId())));
    }

    public static void validateIsAccessingOwnInformation(User requester, Vehicle vehicle) {
        validateIsAccessingOwnInformation(requester, Optional.of(Set.of(vehicle.getUser().getId())));
    }

    public boolean isAuthenticated() {
        return !SecurityContextHolder.getContext().getAuthentication().getName().equalsIgnoreCase("anonymousUser");
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return userService.getByField("username", username);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

}
