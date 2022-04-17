package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.*;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.VehicleDTO;
import com.smart.garage.models.dtos.VehicleFilterDTO;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.services.contracts.VehicleMakeService;
import com.smart.garage.services.contracts.VehicleModelService;
import com.smart.garage.services.contracts.VehicleService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import com.smart.garage.utility.mappers.VehicleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/vehicles")
public class VehiclesMVCController {

    private static final String CREATE_PAGE = "Register new vehicle";
    private static final String EDIT_PAGE = "Edit vehicle";

    private final VehicleService vehicleService;
    private final UserService userService;
    private final VehicleMakeService vehicleMakeService;
    private final VehicleModelService vehicleModelService;
    private final VehicleMapper vehicleMapper;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VehiclesMVCController(VehicleService vehicleService, UserService userService,
                                 VehicleMakeService vehicleMakeService, VehicleModelService vehicleModelService,
                                 VehicleMapper vehicleMapper, UserMapper userMapper,
                                 AuthenticationHelper authenticationHelper) {
        this.vehicleService = vehicleService;
        this.vehicleMakeService = vehicleMakeService;
        this.vehicleModelService = vehicleModelService;
        this.userService = userService;
        this.vehicleMapper = vehicleMapper;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public String getAll(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("vehicleFilterDTO", new VehicleFilterDTO());
            model.addAttribute("vehicles", vehicleService.getAll(currentUser, true));
            return "vehicles";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping()
    public String getAll(@ModelAttribute("vehicleFilterDTO") VehicleFilterDTO vehicleFilterDTO, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("vehicleFilterDTO", vehicleFilterDTO);
            model.addAttribute("vehicles", vehicleService.getAll(currentUser,
                    extractParameter(vehicleFilterDTO.getIdentifierFilter()),
                    extractParameter(vehicleFilterDTO.getOwnerFilter()),
                    extractParameter(vehicleFilterDTO.getBrandFilter()),
                    extractParameter(vehicleFilterDTO.getYearFilter()),
                    extractParameter(vehicleFilterDTO.getSorting()),
                    extractParameter(vehicleFilterDTO.getOrder()),
                    true));
            return "vehicles";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicles";
        }
    }

    @GetMapping("/new")
    public String create(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", CREATE_PAGE);
            model.addAttribute("vehicleDTO", new VehicleDTO());
            return "vehicle-form";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("vehicleDTO") VehicleDTO vehicleDTO,
                         BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", CREATE_PAGE);
            if (errors.hasErrors()) return "vehicle-form";
            Vehicle vehicle = vehicleMapper.toObject(currentUser, vehicleDTO);
            vehicleService.create(currentUser, vehicle);
            return "redirect:/vehicles";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        } catch (IllegalVehicleBrand e) {
            errors.rejectValue("vehicleModelDTO", "invalid_model", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        } catch (DuplicateEntityException e) {
            getDuplicateField(e, "license", "duplicate_license", errors);
            getDuplicateField(e, "VIN", "duplicate_VIN", errors);
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        }
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", EDIT_PAGE);
            Vehicle vehicle = vehicleService.getById(currentUser, id);
            VehicleDTO vehicleDTO = vehicleMapper.toDTO(vehicle);
            model.addAttribute("vehicleDTO", vehicleDTO);
            return "vehicle-form";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id, @Valid @ModelAttribute("vehicleDTO") VehicleDTO vehicleDTO,
                         BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", EDIT_PAGE);
            if (errors.hasErrors()) return "vehicle-form";
            Vehicle original = vehicleService.getById(currentUser, id);
            Vehicle modified = vehicleMapper.toObject(currentUser, vehicleDTO, original);
            vehicleService.update(currentUser, modified);
            return "redirect:/vehicles";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        } catch (IllegalVehicleBrand e) {
            errors.rejectValue("vehicleModelDTO", "invalid_model", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        } catch (DuplicateEntityException e) {
            getDuplicateField(e, "license", "duplicate_license", errors);
            getDuplicateField(e, "VIN", "duplicate_VIN", errors);
            model.addAttribute("errorMessage", e.getMessage());
            return "vehicle-form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            vehicleService.softDelete(currentUser, id);
            return "redirect:/vehicles";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    public void addPageAttributes(User requester, Model model) {
        model.addAttribute("users", userService.getAllFiltered(UserRoles.CUSTOMER.getValue(), requester,
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty()).stream().filter(u -> !u.isDeleted())
                .map(userMapper::toDTOOut).collect(Collectors.toList()));
        model.addAttribute("makes", vehicleMakeService.getAll(requester));
        model.addAttribute("models", vehicleModelService.getAll(requester));
        model.addAttribute("brands", getBrands(requester));
        model.addAttribute("years", getYears());
    }

    public List<String> getBrands(User requester) {
        List<String> brands = new ArrayList<>();
        brands.addAll(vehicleMakeService.getAll(requester).stream().map(Object::toString).collect(Collectors.toList()));
        brands.addAll(vehicleModelService.getAll(requester).stream().map(Object::toString).collect(Collectors.toList()));
        brands.sort(Comparator.comparing(Object::toString, String.CASE_INSENSITIVE_ORDER));
        return brands;
    }

    public static List<Integer> getYears() {
        List<Integer> years = new ArrayList<>();
        for (int i = LocalDateTime.now().getYear(); i >= 1886; i--) {
            years.add(i);
        }
        return years;
    }

    private Optional<String> extractParameter(String parameter) {
        return Optional.ofNullable(parameter.isEmpty() ? null : parameter);
    }

    private <T> Optional<Set<T>> extractParameter(Set<T> parameter) {
        return Optional.ofNullable(parameter == null || parameter.isEmpty() ? null : parameter);
    }

    private void getDuplicateField(DuplicateEntityException e, String field, String label, BindingResult errors) {
        if (e.getMessage().contains(field)) {
            errors.rejectValue(field, label, e.getMessage().replace("Vehicle", "Vehicle "));
        }
    }
}
