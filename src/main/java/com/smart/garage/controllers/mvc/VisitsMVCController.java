package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.PhotoDTO;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/visits")
public class VisitsMVCController {

    private static final String CREATE_PAGE = "Register new visit";
    private static final String EDIT_PAGE = "Edit visit";

    private final VisitService visitService;
    private final VehicleService vehicleService;
    private final UserService userService;
    private final ServicesService servicesService;
    private final VisitMapper visitMapper;
    private final UserMapper userMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VisitsMVCController(VisitService visitService, VehicleService vehicleService,
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

    @GetMapping("/{id}")
    public String showVisitPage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            Visit visit = visitService.getById(currentUser, id);
            model.addAttribute("visit", visit);
            model.addAttribute("currencies", Currencies.values());
            PhotoDTO photo = new PhotoDTO();
            photo.setVisitId(id);
            model.addAttribute("photoDTO", photo);
            return "visit";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
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
        model.addAttribute("users", userService.getAllFiltered(UserRoles.CUSTOMER.getValue(),
                        currentUser, Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty())
                .stream().filter(u -> !u.isDeleted()).map(userMapper::toDTOOut).collect(Collectors.toList()));
        model.addAttribute("vehicles", vehicleService.getAll(currentUser, true));
        model.addAttribute("status", visitService.getStatus());
        model.addAttribute("services", servicesService.getAll(Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.of("id"), Optional.empty()));
        model.addAttribute("currencies", Currencies.values());
    }

    private <T> Optional<Set<T>> extractParameter(Set<T> parameter) {
        return Optional.ofNullable(parameter == null || parameter.isEmpty() ? null : parameter);
    }

    private Optional<String> extractParameter(String parameter) {
        return Optional.ofNullable(parameter.isEmpty() ? null : parameter);
    }

    private Optional<LocalDateTime> extractParameter(Date parameter) {
        return Optional.ofNullable(parameter == null ? null :
                LocalDateTime.ofInstant(parameter.toInstant(), ZoneId.systemDefault()));
    }


    @GetMapping("/customer")
    public String getMyVisits(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addMyAttributes(currentUser, model);
            model.addAttribute("visitFilterDTO", new VisitFilterDTO());
            model.addAttribute("visits", visitService.getAll(currentUser,
                    Optional.of(Set.of(currentUser.getId())),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    false));
            return "visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/customer")
    public String getMyVisits(@ModelAttribute("visitFilterDTO") VisitFilterDTO visitFilterDTO, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addMyAttributes(currentUser, model);
            model.addAttribute("visitFilterDTO", visitFilterDTO);
            model.addAttribute("visits", visitService.getAll(currentUser,
                    Optional.of(Set.of(currentUser.getId())),
                    extractParameter(visitFilterDTO.getVehicleFilter()),
                    extractParameter(visitFilterDTO.getStatusFilter()),
                    extractParameter(visitFilterDTO.getDateFrom()),
                    extractParameter(visitFilterDTO.getDateTo()),
                    extractParameter(visitFilterDTO.getSorting()),
                    extractParameter(visitFilterDTO.getOrder()),
                    false));
            return "visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "visits";
        }
    }

    private void addMyAttributes(User currentUser, Model model) {
        model.addAttribute("vehicles", vehicleService.getAll(currentUser, Optional.empty(),
                Optional.of(Set.of(currentUser.getId())), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true));
        model.addAttribute("status", visitService.getStatus());
        model.addAttribute("services", servicesService.getAll(Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.of("id"), Optional.empty()));
        model.addAttribute("currencies", Currencies.values());
    }
}
