package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.*;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.NewCustomerDTO;
import com.smart.garage.models.dtos.PhotoDTO;
import com.smart.garage.services.contracts.*;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.ReportProducerHelper;
import com.smart.garage.utility.mappers.UserMapper;
import com.smart.garage.utility.mappers.VehicleMapper;
import com.smart.garage.utility.mappers.VisitMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.smart.garage.services.ServicesServiceImpl.RESTRICTED_FOR_EMPLOYEES;

@Controller
@RequestMapping("/visits")
public class VisitExtrasMVCController {

    private final VisitService visitService;
    private final VehicleMakeService vehicleMakeService;
    private final VehicleModelService vehicleModelService;
    private final ServicesService servicesService;
    private final PhotoService photoService;
    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final VisitMapper visitMapper;
    private final ReportProducerHelper reportProducerHelper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public VisitExtrasMVCController(VisitService visitService,
                                    VehicleMakeService vehicleMakeService, VehicleModelService vehicleModelService,
                                    ServicesService servicesService, PhotoService photoService,
                                    UserMapper userMapper, VehicleMapper vehicleMapper, VisitMapper visitMapper,
                                    ReportProducerHelper reportProducerHelper,
                                    AuthenticationHelper authenticationHelper) {
        this.visitService = visitService;
        this.vehicleMakeService = vehicleMakeService;
        this.vehicleModelService = vehicleModelService;
        this.servicesService = servicesService;
        this.photoService = photoService;
        this.userMapper = userMapper;
        this.vehicleMapper = vehicleMapper;
        this.visitMapper = visitMapper;
        this.reportProducerHelper = reportProducerHelper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping("/new-customer")
    public String createVisitForNewCustomer(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            model.addAttribute("newCustomerDTO", new NewCustomerDTO());
            return "visit-new-customer";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

    @PostMapping("/new-customer")
    public String createVisitForNewCustomer(@Valid @ModelAttribute("newCustomerDTO") NewCustomerDTO newCustomerDTO, BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            addPageAttributes(currentUser, model);
            if (errors.hasErrors()) return "visit-new-customer";
            User user = userMapper.toObject(newCustomerDTO);
            Vehicle vehicle = vehicleMapper.toObject(currentUser, newCustomerDTO);
            Visit visit = visitMapper.toObject(currentUser, newCustomerDTO);
            Set<Integer> services = newCustomerDTO.getServiceIDs();
            visitService.create(currentUser, user, vehicle, visit, services);
            return "redirect:/visits";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (IllegalVehicleBrand e) {
            errors.rejectValue("vehicleModelDTO", "invalid_model", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-new-customer";
        } catch (InvalidParameter e) {
            errors.rejectValue("startDate", "invalid_date", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-new-customer";
        } catch (DuplicateEntityException e) {
            getDuplicateField(e, "username", "duplicate_username", errors);
            getDuplicateField(e, "email", "duplicate_email", errors);
            getDuplicateField(e, "phoneNumber", "duplicate_phoneNumber", errors);
            getDuplicateField(e, "license", "duplicate_license", errors);
            getDuplicateField(e, "VIN", "duplicate_VIN", errors);
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-new-customer";
        } catch (EntityNotFoundException e) {
            errors.rejectValue("serviceIDs", "missing_services", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "visit-new-customer";
        }
    }

    @PostMapping("/generate-report")
    public String generateReport(@RequestParam(name = "selectedVisits", required = false) int[] selectedVisits,
                                 @RequestParam(name = "selectedCurrency", required = false) String currency,
                                 Model model) {
        //TODO Our commercial license for iText (used by methods that service this controller)
        //TODO expires on the 4th May (we can get a new one just before presenting)

        //TODO our free trial for FOREX Currency Exchange expires on 10 April
        //TODO our second free trial for FOREX expires on 18 April
        User currentUser = authenticationHelper.getCurrentUser();

        Currencies selectedCurrency = Enum.valueOf(Currencies.class, currency);

        try {
            reportProducerHelper.generateReport(currentUser, selectedVisits, selectedCurrency);
            return "visit-report-produced";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "There was an issue with producing the report you requested. Please try again.");
            return "not-found";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }


    @PostMapping("/{id}/photos")
    public String uploadPhoto(@PathVariable int id,
                              @Valid @ModelAttribute("photoDTO") PhotoDTO photoDTO,
                              BindingResult errors, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getRole().getName() == UserRoles.CUSTOMER) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        if (errors.hasErrors()) {
            return "visit-photo-upload";
        }

        if (photoDTO.getPhoto() == null || photoDTO.getPhoto().isEmpty()) {
            return "redirect:/visits/" + id;
        }

        try {
            photoService.save(currentUser, photoDTO.getPhoto(), id);
            return "redirect:/visits/" + id;
        } catch (IOException e) {
            model.addAttribute("errorMessage", "There was an issue with the upload of your photo. Please try again.");
            return "not-found";
        }
    }

    @GetMapping("/{id}/photos/{token}/delete")
    public String deletePhoto(@PathVariable int id, @PathVariable String token, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getRole().getName() == UserRoles.CUSTOMER) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        try {
            photoService.delete(currentUser, id, token);
            return "redirect:/visits/" + id;
        } catch (IOException e) {
            model.addAttribute("errorMessage", "There was deleting the photo you selected. Please try again.");
            return "not-found";
        }
    }

    private void addPageAttributes(User currentUser, Model model) {
        model.addAttribute("makes", vehicleMakeService.getAll(currentUser));
        model.addAttribute("models", vehicleModelService.getAll(currentUser));
        model.addAttribute("years", getYears());
        model.addAttribute("status", visitService.getStatus());
        model.addAttribute("services", servicesService.getAll(Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.of("id"), Optional.empty()));
    }

    private List<Integer> getYears() {
        List<Integer> years = new ArrayList<>();
        for (int i = LocalDateTime.now().getYear(); i >= 1886; i--) {
            years.add(i);
        }
        return years;
    }

    private void getDuplicateField(DuplicateEntityException e, String field, String label, BindingResult errors) {
        if (e.getMessage().contains(field)) {
            errors.rejectValue(field, label, e.getMessage().replace("Vehicle", "Vehicle "));
        }
    }

}
