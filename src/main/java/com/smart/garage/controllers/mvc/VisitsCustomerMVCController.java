package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Currencies;
import com.smart.garage.models.User;
import com.smart.garage.models.Visit;
import com.smart.garage.models.dtos.PhotoDTO;
import com.smart.garage.models.dtos.VisitDTO;
import com.smart.garage.models.dtos.VisitFilterDTO;
import com.smart.garage.services.StripeServiceImpl;
import com.smart.garage.services.contracts.ServicesService;
import com.smart.garage.services.contracts.VehicleService;
import com.smart.garage.services.contracts.VisitService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.VisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

import static com.smart.garage.utility.VisitDataExtractor.extractParameter;

@Controller
@RequestMapping("/visits")
public class VisitsCustomerMVCController {

    public static final String PUBLIC_KEY = "pk_test_51KqWsVJk66hxMyhp7HZlPl1mrAdRt5xj8lxP7n7EanB4xJpiBbfnxnZA54UipbkPlZ8THFqebwRt5QdvBiSNlOTy00NWvBi2sv";
    private static final String REQUEST_PAGE = "Request New Visit";
    private final VisitService visitService;
    private final VehicleService vehicleService;
    private final ServicesService servicesService;
    private final VisitMapper visitMapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VisitsCustomerMVCController(VisitService visitService, VehicleService vehicleService,
                                       ServicesService servicesService, VisitMapper visitMapper,
                                       AuthenticationHelper authenticationHelper, StripeServiceImpl paymentsService) {
        this.visitService = visitService;
        this.vehicleService = vehicleService;
        this.servicesService = servicesService;
        this.visitMapper = visitMapper;
        this.authenticationHelper = authenticationHelper;
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

    @GetMapping("/{id}")
    public String showVisitPage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            Visit visit = visitService.getById(currentUser, id);
            model.addAttribute("visit", visit);
            model.addAttribute("currencies", Currencies.values());
            model.addAttribute("stripePublicKey", PUBLIC_KEY);
            model.addAttribute("amount", visit.getTotalCost() * 100);
            model.addAttribute("currency", Currencies.BGN);
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

    @GetMapping("/request")
    public String requestNewVisit(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addMyAttributes(currentUser, model);
            model.addAttribute("pageTitle", REQUEST_PAGE);
            model.addAttribute("visitDTO", new VisitDTO());
            return "visit-form";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/request")
    public String requestNewVisit(@Valid @ModelAttribute("visitDTO") VisitDTO visitDTO,
                                  BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addMyAttributes(currentUser, model);
            model.addAttribute("pageTitle", REQUEST_PAGE);
            if (errors.hasErrors()) return "visit-form";
            Visit visit = visitMapper.toObject(currentUser, visitDTO);
            visitService.create(currentUser, visit, visitDTO.getServiceIDs());
            return "redirect:/visits/customer";
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
