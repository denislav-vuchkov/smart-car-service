package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.UserFilterDTO;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.smart.garage.controllers.mvc.CustomersMVCController.SORT_BY_ID;

@Controller
@RequestMapping("/employees")
public class EmployeesMVCController {

    private final UserMapper mapper;
    private final UserService service;
    private final AuthenticationHelper authenticationHelper;

    public EmployeesMVCController(UserMapper mapper, UserService service, AuthenticationHelper authenticationHelper) {
        this.mapper = mapper;
        this.service = service;
        this.authenticationHelper = authenticationHelper;
    }


    @GetMapping()
    public String showEmployeesPage(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            List<User> customersList = service.getAllFiltered(UserRoles.EMPLOYEE.getValue(), currentUser, Optional.empty(),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.of(SORT_BY_ID), Optional.empty());

            List<UserDTOOut> employeeList = customersList.stream().map(mapper::toDTOOut).collect(Collectors.toList());
            model.addAttribute("employees", employeeList);

            UserFilterDTO filterDTO = new UserFilterDTO();
            model.addAttribute("filterDTO", filterDTO);

            return "employees";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping()
    public String filterEmployeesPage(@ModelAttribute("filterDTO") UserFilterDTO filterDTO, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            List<User> employees = service.getAllFiltered(UserRoles.EMPLOYEE.getValue(), currentUser,
                    Optional.of(filterDTO.getUsername()), Optional.of(filterDTO.getEmail()),
                    Optional.of(filterDTO.getPhoneNumber()), Optional.empty(), Optional.empty(),
                    Optional.empty(), Optional.of(filterDTO.getSortBy()), Optional.of(filterDTO.getSortOrder()));

            List<UserDTOOut> employeesList = employees.stream().map(mapper::toDTOOut).collect(Collectors.toList());

            model.addAttribute("employees", employeesList);
            model.addAttribute("filterDTO", filterDTO);

            return "employees";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }


}
