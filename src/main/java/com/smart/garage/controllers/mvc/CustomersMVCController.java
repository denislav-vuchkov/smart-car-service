package com.smart.garage.controllers.mvc;

import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.UserFilterDTO;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customers")
public class CustomersMVCController {

    public static final String SORT_BY_ID = "u.id";
    private final UserService service;
    private final UserMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public CustomersMVCController(UserService service, UserMapper mapper, AuthenticationHelper authenticationHelper) {
        this.service = service;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public String showCustomersPage(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            List<User> customersList = service.getAllFiltered(UserRoles.CUSTOMER.getValue(), currentUser, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.of(SORT_BY_ID), Optional.empty());
            List<UserDTOOut> customerDTOOuts = customersList.stream().map(mapper::toDTOOut).collect(Collectors.toList());
            model.addAttribute("customers", customerDTOOuts);
            UserFilterDTO filterDTO = new UserFilterDTO();
            model.addAttribute("filterDTO", filterDTO);
            return "customers";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }

    }

    @PostMapping()
    public String filterCustomersPage(@ModelAttribute("filterDTO") UserFilterDTO filterDTO, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            List<User> customers = service.getAllFiltered(UserRoles.CUSTOMER.getValue(), currentUser,
                    Optional.ofNullable(filterDTO.getUsername().isBlank() ? null : filterDTO.getUsername()),
                    Optional.ofNullable(filterDTO.getEmail().isBlank() ? null : filterDTO.getEmail()),
                    Optional.ofNullable(filterDTO.getPhoneNumber().isBlank() ? null : filterDTO.getPhoneNumber()),
                    Optional.ofNullable(filterDTO.getLicenseOrVIN().isBlank() ? null : filterDTO.getLicenseOrVIN()),
                    Optional.ofNullable(filterDTO.getMake().isBlank() ? null : filterDTO.getMake()),
                    Optional.ofNullable(filterDTO.getModel().isBlank() ? null : filterDTO.getModel()),
                    Optional.of(filterDTO.getSortBy()), Optional.of(filterDTO.getSortOrder()));

            List<UserDTOOut> customerDTOOuts = customers.stream().map(mapper::toDTOOut).collect(Collectors.toList());
            model.addAttribute("customers", customerDTOOuts);
            model.addAttribute("filterDTO", filterDTO);
            return "customers";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

}
