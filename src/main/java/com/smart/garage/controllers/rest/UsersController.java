package com.smart.garage.controllers.rest;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.ContactDetailsDTO;
import com.smart.garage.models.dtos.UserDTOIn;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.UserPasswordDTO;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    public static final String EMAIL_SEND_FAILED_MESSAGE = "There was an issue with sending an email to the user being registered. Please try again.";
    private final UserService service;
    private final UserMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public UsersController(UserService service, UserMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public List<UserDTOOut> getAll() {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            return service.getAll(currentUser).stream().map(mapper::toDTOOut).collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public UserDTOOut getById(@PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User requestedUser = service.getById(currentUser, id);
            return mapper.toDTOOut(requestedUser);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/customers")
    public List<UserDTOOut> getAllCustomersFiltered(@RequestParam(required = false) Optional<String> username,
                                          @RequestParam(required = false) Optional<String> email,
                                          @RequestParam(required = false) Optional<String> phoneNumber,
                                          @RequestParam(required = false) Optional<String> licenseOrVIN,
                                          @RequestParam(required = false) Optional<String> make,
                                          @RequestParam(required = false) Optional<String> model,
                                          @RequestParam(required = false) Optional<String> sortBy,
                                          @RequestParam(required = false) Optional<String> sortOrder) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            return service.getAllFiltered(UserRoles.CUSTOMER.getValue(), currentUser, username,
                            email, phoneNumber, licenseOrVIN, make, model, sortBy, sortOrder)
                    .stream().map(mapper::toDTOOut).collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/employees")
    public List<UserDTOOut> getAllEmployeesFiltered(@RequestParam(required = false) Optional<String> username,
                                                    @RequestParam(required = false) Optional<String> email,
                                                    @RequestParam(required = false) Optional<String> phoneNumber,
                                                    @RequestParam(required = false) Optional<String> licenseOrVIN,
                                                    @RequestParam(required = false) Optional<String> make,
                                                    @RequestParam(required = false) Optional<String> model,
                                                    @RequestParam(required = false) Optional<String> sortBy,
                                                    @RequestParam(required = false) Optional<String> sortOrder) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            return service.getAllFiltered(UserRoles.EMPLOYEE.getValue(), currentUser, username,
                            email, phoneNumber, licenseOrVIN, make, model, sortBy, sortOrder)
                    .stream().map(mapper::toDTOOut).collect(Collectors.toList());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/new")
    public UserDTOOut create(@Valid @RequestBody UserDTOIn userDTOIn) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User user = mapper.toObject(userDTOIn);
            return mapper.toDTOOut(service.create(currentUser, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, EMAIL_SEND_FAILED_MESSAGE);
        }
    }

    @PutMapping("/{id}")
    public UserDTOOut update(@Valid @RequestBody UserDTOIn userDTOIn, @PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User user = mapper.toObject(currentUser, id, userDTOIn);
            return mapper.toDTOOut(service.update(currentUser, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/contact")
    public UserDTOOut updateContactDetails(@Valid @RequestBody ContactDetailsDTO contactDetailsDTO, @PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User user = mapper.toObject(currentUser, id, contactDetailsDTO);
            return mapper.toDTOOut(service.updateContactDetails(currentUser, user));
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}/password")
    public void updatePassword(@Valid @RequestBody UserPasswordDTO userPasswordDTO, @PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (!userPasswordDTO.getPassword().equals(userPasswordDTO.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your passwords do not match.");
        }

        try {
            service.updatePassword(currentUser, id, userPasswordDTO.getPassword());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable int id) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            service.softDelete(currentUser, id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("forgotten-password")
    public void sendResetPasswordEmail(@RequestParam(name = "email") String email) {
        try {
            User user = service.getByField("email", email);
            service.generateResetPasswordEmail(user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
