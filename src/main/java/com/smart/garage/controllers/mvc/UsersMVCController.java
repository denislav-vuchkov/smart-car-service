package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Role;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.ContactDetailsDTO;
import com.smart.garage.models.dtos.UserDTOIn;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.UserPasswordDTO;
import com.smart.garage.services.contracts.RolesService;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.smart.garage.services.ServicesServiceImpl.RESTRICTED_FOR_EMPLOYEES;
import static com.smart.garage.services.ServicesServiceImpl.RESTRICTED_FOR_OWNER;

@Controller
@RequestMapping("/users")
public class UsersMVCController {

    public static final String RESTRICTED_FOR_OWNER_MESSAGE = "You are not authorised to edit the details of other employees or admins.";
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper mapper;
    private final RolesService rolesService;

    @Autowired
    public UsersMVCController(UserService userService, AuthenticationHelper authenticationHelper,
                              UserMapper mapper, RolesService rolesService) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.mapper = mapper;
        this.rolesService = rolesService;
    }

    @GetMapping("/{id}")
    public String showUserPage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User user = userService.getById(currentUser, id);
            model.addAttribute("user", mapper.toDTOOut(user));
            return "user";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @GetMapping("/new")
    public String showRegisterUserPage(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getRole().getId() == UserRoles.CUSTOMER.getValue()) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        model.addAttribute("userDTO", new UserDTOIn());
        return "users-new";
    }

    @PostMapping("/new")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTOIn userDTOIn,
                               BindingResult errors,
                               Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (errors.hasErrors()) {
            return "users-new";
        }

        try {
            User user = mapper.toObject(userDTOIn);
            userService.create(currentUser, user);
            return "redirect:/users/" + user.getId();
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (DuplicateEntityException e) {
            if (e.getMessage().contains("username")) {
                errors.rejectValue("username", "duplicate_username", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                errors.rejectValue("email", "duplicate_email", e.getMessage());
            } else {
                errors.rejectValue("phoneNumber", "duplicate_phoneNumber", e.getMessage());
            }
            return "users-new";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @ModelAttribute("roleList")
    public List<Role> roleList() {
        return rolesService.getAll();
    }

    @GetMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User userToDelete = userService.getById(currentUser, id);
            if (userToDelete.getRole().getName() == UserRoles.EMPLOYEE && currentUser.getId() != id) {
                throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER_MESSAGE);
            }

            userService.softDelete(currentUser, id);
            if (currentUser.getId() == id) {
                SecurityContextHolder.clearContext();
                return "redirect:/";
            }

            if (userToDelete.getRole().getName() == UserRoles.EMPLOYEE) {
                return "redirect:/employees";
            } else {
                return "redirect:/customers";
            }
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException | DuplicateEntityException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }


    @GetMapping("/{id}/update")
    public String showUpdatePage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getId() == id && currentUser.getRole().getName().getValue() == UserRoles.CUSTOMER.getValue()) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        try {
            User user = userService.getById(currentUser, id);

            if (user.getRole().getName().getValue() == UserRoles.EMPLOYEE.getValue() && id != currentUser.getId()) {
                throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER_MESSAGE);
            }

            UserDTOIn userDTOIn = mapper.toDTOIn(user);
            model.addAttribute("userDTO", userDTOIn);
            return "user-update";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable int id,
                             @Valid @ModelAttribute("userDTO") UserDTOIn userDTOIn,
                             BindingResult errors,
                             Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getId() == id && currentUser.getRole().getName().getValue() == UserRoles.CUSTOMER.getValue()) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        if (errors.hasErrors()) {
            return "user-update";
        }

        try {
            User user = mapper.toObject(currentUser, id, userDTOIn);
            userService.update(currentUser, user);
            if (currentUser.getId() == id) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            return "redirect:/users/" + id;
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (DuplicateEntityException e) {
            if (e.getMessage().contains("username")) {
                errors.rejectValue("username", "duplicate_username", e.getMessage());
            } else if (e.getMessage().contains("email")) {
                errors.rejectValue("email", "duplicate_email", e.getMessage());
            } else {
                errors.rejectValue("phoneNumber", "duplicate_phoneNumber", e.getMessage());
            }
            return "user-update";
        }
    }

    @GetMapping("/{id}/update-contact")
    public String showUpdateContactsPage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getId() != id || currentUser.getRole().getName() != UserRoles.CUSTOMER) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_OWNER);
            return "unauthorised";
        }

        try {
            User user = userService.getById(currentUser, id);

            ContactDetailsDTO contactDetailsDTO = mapper.toContactsDTO(user);
            model.addAttribute("contactsDTO", contactDetailsDTO);
            return "user-update-contact";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update-contact")
    public String updateUserContactDetails(@PathVariable int id,
                                           @Valid @ModelAttribute("contactsDTO") ContactDetailsDTO contactDetailsDTO,
                                           BindingResult errors,
                                           Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        model.addAttribute("contactsDTO", contactDetailsDTO);

        if (errors.hasErrors()) {
            return "user-update-contact";
        }

        try {
            User user = mapper.toObject(currentUser, id, contactDetailsDTO);
            userService.updateContactDetails(currentUser, user);
            return "redirect:/users/" + id;
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (DuplicateEntityException e) {
            if (e.getMessage().contains("email")) {
                errors.rejectValue("email", "duplicate_email", e.getMessage());
            } else {
                errors.rejectValue("phoneNumber", "duplicate_phoneNumber", e.getMessage());
            }
            return "user-update-contact";
        }
    }


    @GetMapping("/{id}/update-password")
    public String showUpdatePasswordPage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            User user = userService.getById(currentUser, id);

            if (currentUser.getId() != user.getId()) {
                model.addAttribute("errorMessage", RESTRICTED_FOR_OWNER);
                return "unauthorised";
            }

            model.addAttribute("userDTO", mapper.toDTOOut(user));
            model.addAttribute("passwordDTO", new UserPasswordDTO());
            return "user-update-password";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update-password")
    public String updateUserPassword(@PathVariable int id,
                                     @Valid @ModelAttribute("passwordDTO") UserPasswordDTO passwordDTO,
                                     BindingResult errors,
                                     @ModelAttribute("userDTO") UserDTOOut userDTOOut,
                                     Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        model.addAttribute("userDTO", userDTOOut);

        if (errors.hasErrors()) {
            return "user-update-password";
        }

        if (!passwordDTO.getPassword().equals(passwordDTO.getConfirmPassword())) {
            errors.rejectValue("password", "password_mismatch", "Password do not match!");
            return "user-update-password";
        }

        try {
            if (currentUser.getId() != userDTOOut.getId()) {
                throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER_MESSAGE);
            }

            userService.updatePassword(currentUser, id, passwordDTO.getPassword());
            return "redirect:/users/" + id;
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

}
