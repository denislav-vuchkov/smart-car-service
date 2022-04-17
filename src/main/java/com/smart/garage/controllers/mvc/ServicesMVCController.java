package com.smart.garage.controllers.mvc;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.ServicesDTOIn;
import com.smart.garage.models.dtos.ServicesFilterDTO;
import com.smart.garage.services.contracts.ServicesService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.mappers.ServicesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.smart.garage.services.ServicesServiceImpl.RESTRICTED_FOR_EMPLOYEES;

@Controller
@RequestMapping("/services")
public class ServicesMVCController {

    private final ServicesService servicesService;
    private final ServicesMapper mapper;
    private final AuthenticationHelper authenticationHelper;

    @Autowired
    public ServicesMVCController(ServicesService servicesService,
                                 ServicesMapper mapper,
                                 AuthenticationHelper authenticationHelper) {
        this.servicesService = servicesService;
        this.mapper = mapper;
        this.authenticationHelper = authenticationHelper;
    }

    @GetMapping()
    public String getAll(Model model) {
        List<Servicez> servicezList = servicesService.getAll(Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of("id"), Optional.empty());
        model.addAttribute("services", servicezList);

        Servicez mostExpensiveService = servicesService.getMostExpensiveService();
        ServicesFilterDTO filterDTO = new ServicesFilterDTO();
        filterDTO.setPriceMaximum(mostExpensiveService.getPriceBGN());
        model.addAttribute("filterDTO", filterDTO);
        model.addAttribute("mostExpensiveService", mostExpensiveService);

        return "services";
    }

    @PostMapping("")
    public String getAll(@ModelAttribute("filterDTO") ServicesFilterDTO filterDTO, Model model) {
        List<Servicez> servicezList = servicesService.getAll(
                Optional.ofNullable(filterDTO.getName().isEmpty() ? null : filterDTO.getName()),
                Optional.of(filterDTO.getPriceMinimum() == 0 ? 0 : filterDTO.getPriceMinimum()),
                Optional.of(filterDTO.getPriceMaximum() == 0 ? Integer.MAX_VALUE : filterDTO.getPriceMaximum()),
                Optional.ofNullable(filterDTO.getSortBy()),
                Optional.ofNullable(filterDTO.getSortOrder()));

        model.addAttribute("services", servicezList);
        model.addAttribute("filterDTO", filterDTO);
        model.addAttribute("mostExpensiveService", servicesService.getMostExpensiveService());

        return "services";
    }

    @GetMapping("/new")
    public String getServicesPage(Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (currentUser.getRole().getName() == UserRoles.CUSTOMER) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        model.addAttribute("newServiceDTO", new ServicesDTOIn());
        return "service-new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("newServiceDTO") ServicesDTOIn newServiceDTOIn,
                         BindingResult errors,
                         Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (errors.hasErrors()) {
            return "service-new";
        }

        Servicez service = mapper.toObject(newServiceDTOIn);

        try {
            this.servicesService.create(currentUser, service);
            return "redirect:/services";
        } catch (DuplicateEntityException e) {
            model.addAttribute("isNewServiceSuccess", false);
            errors.rejectValue("serviceName", "name_error" ,e.getMessage());
            return "service-new";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }


    @GetMapping("/{id}/update")
    public String showUpdatePage(@PathVariable int id, Model model) {
        User currentUser = authenticationHelper.getCurrentUser();
        if (currentUser.getRole().getId() == UserRoles.CUSTOMER.getValue()) {
            model.addAttribute("errorMessage", RESTRICTED_FOR_EMPLOYEES);
            return "unauthorised";
        }

        try {
            Servicez service = servicesService.getById(id);

            ServicesDTOIn servicesDTOIn = new ServicesDTOIn();
            servicesDTOIn.setServiceName(service.getName());
            servicesDTOIn.setPrice(service.getPriceBGN());
            model.addAttribute("serviceDTO", servicesDTOIn);
            return "service-update";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @PostMapping("/{id}/update")
    public String updateService(@PathVariable int id,
                                 @Valid @ModelAttribute("serviceDTO") ServicesDTOIn servicesDTO,
                                 BindingResult errors,
                                 Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        if (errors.hasErrors()) {
            return "service-update";
        }

        try {
            Servicez service = servicesService.getById(id);

            service.setName(servicesDTO.getServiceName());
            service.setPriceBGN(servicesDTO.getPrice());

            servicesService.update(currentUser, service);

            return "redirect:/services";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (DuplicateEntityException e) {
            errors.rejectValue("serviceName", "name_duplicate", e.getMessage());
            return "service-update";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }


    @GetMapping("/{id}/delete")
    public String deleteService(@PathVariable int id,
                                 Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            servicesService.delete(currentUser, id);
            return "redirect:/services";
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        }
    }

}
