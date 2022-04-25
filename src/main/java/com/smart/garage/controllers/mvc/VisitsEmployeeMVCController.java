package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.models.dtos.VisitDTO;
import com.smart.garage.models.dtos.VisitFilterDTO;
import com.smart.garage.services.contracts.ServicesService;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.services.contracts.VehicleService;
import com.smart.garage.services.contracts.VisitService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.UserMapper;
import com.smart.garage.utility.mappers.VisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.smart.garage.utility.VisitDataExtractor.extractParameter;

@Controller
@RequestMapping("/visits")
public class VisitsEmployeeMVCController {

    private static final String CREATE_PAGE = "Register New Visit";
    private static final String EDIT_PAGE = "Edit Visit";

    private final VisitService visitService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final ServicesService servicesService;
    private final VisitMapper visitMapper;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VisitsEmployeeMVCController(VisitService visitService, VehicleService vehicleService,
                                       UserService userService, ServicesService servicesService,
                                       VisitMapper visitMapper, UserMapper userMapper,
                                       AuthenticationHelper authenticationHelper) {
        this.visitService = visitService;
        this.vehicleService = vehicleService;
        this.userService = userService;
        this.servicesService = servicesService;
        this.visitMapper = visitMapper;
        this.userMapper = userMapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public String getAll(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("visitFilterDTO", new VisitFilterDTO());
            model.addAttribute("visits", visitService.getAll(currentUser, true));
            return "visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping()
    public String getAll(@ModelAttribute("visitFilterDTO") VisitFilterDTO visitFilterDTO, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("visitFilterDTO", visitFilterDTO);
            model.addAttribute("visits", visitService.getAll(currentUser,
                    extractParameter(visitFilterDTO.getOwnerFilter()),
                    extractParameter(visitFilterDTO.getVehicleFilter()),
                    extractParameter(visitFilterDTO.getStatusFilter()),
                    extractParameter(visitFilterDTO.getDateFrom()),
                    extractParameter(visitFilterDTO.getDateTo()),
                    extractParameter(visitFilterDTO.getSorting()),
                    extractParameter(visitFilterDTO.getOrder()),
                    true));
            return "visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "visits";
        }
    }

    @GetMapping("/new")
    public String create(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", CREATE_PAGE);
            model.addAttribute("visitDTO", new VisitDTO());
            return "visit-form";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("visitDTO") VisitDTO visitDTO, BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", CREATE_PAGE);
            if (errors.hasErrors()) return "visit-form";
            Visit visit = visitMapper.toObject(currentUser, visitDTO);
            visitService.create(currentUser, visit, visitDTO.getServiceIDs());
            return "redirect:/visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            errors.rejectValue("serviceIDs", "missing_services", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-form";
        } catch (InvalidParameter e) {
            errors.rejectValue("startDate", "invalid_date", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-form";
        }
    }

    @GetMapping("/{id}/accept")
    public String accept(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            visitService.accept(currentUser, id);
            return "redirect:/visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", EDIT_PAGE);
            Visit visit = visitService.getById(currentUser, id);
            VisitDTO visitDTO = visitMapper.toDTO(visit);
            model.addAttribute("visitDTO", visitDTO);
            return "visit-form";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id, @Valid @ModelAttribute("visitDTO") VisitDTO visitDTO,
                         BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("pageTitle", EDIT_PAGE);
            if (errors.hasErrors()) return "visit-form";
            Visit original = visitService.getById(currentUser, id);
            Visit modified = visitMapper.toObject(currentUser, visitDTO, original);
            visitService.update(currentUser, modified, visitDTO.getServiceIDs());
            return "redirect:/visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            if (e.getMessage().contains("Service")) {
                errors.rejectValue("serviceIDs", "missing_services", e.getMessage());
                model.addAttribute("errorMessage", e.getMessage());
                return "visit-form";
            } else {
                model.addAttribute("errorMessage", e.getMessage());
                return "not-found";
            }
        } catch (InvalidParameter e) {
            errors.rejectValue("startDate", "invalid_date", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-form";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            visitService.softDelete(currentUser, id);
            return "redirect:/visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    private void addPageAttributes(User currentUser, Model model) {
        model.addAttribute("users", getActiveCustomers(currentUser));
        model.addAttribute("vehicles", vehicleService.getAll(currentUser, true));
        model.addAttribute("status", getActiveStatusLabels());
        model.addAttribute("services", getServicesSortedByID());
        model.addAttribute("currencies", Currencies.values());
    }

    private List<UserDTOOut> getActiveCustomers(User currentUser) {
        return userService.getAllFiltered(UserRoles.CUSTOMER.getValue(), currentUser,
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty()).stream()
                .filter(u -> !u.isDeleted()).map(userMapper::toDTOOut).collect(Collectors.toList());
    }

    private List<VisitStatus> getActiveStatusLabels() {
        return visitService.getStatus().stream()
                .filter(s -> !s.getName().matches("Declined")).collect(Collectors.toList());
    }

    private List<Servicez> getServicesSortedByID() {
        return servicesService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of("id"), Optional.empty());
    }

}
