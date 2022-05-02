package com.smart.garage.controllers.mvc;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.smart.garage.exceptions.*;
import com.smart.garage.models.*;
import com.smart.garage.models.dtos.ChargeRequest;
import com.smart.garage.models.dtos.NewCustomerDTO;
import com.smart.garage.models.dtos.PhotoDTO;
import com.smart.garage.services.contracts.*;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.ForexCurrencyExchange;
import com.smart.garage.utility.ReportProducerHelper;
import com.smart.garage.utility.SMSHelper;
import com.smart.garage.utility.mappers.PaymentRecordsMapper;
import com.smart.garage.utility.mappers.UserMapper;
import com.smart.garage.utility.mappers.VehicleMapper;
import com.smart.garage.utility.mappers.VisitMapper;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.twilio.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.smart.garage.utility.AuthenticationHelper.RESTRICTED_FOR_EMPLOYEES;

@Controller
@RequestMapping("/visits")
public class VisitExtrasMVCController {

    public static final int PAYPAL = 1;
    public static final int STRIPE = 2;
    public static final String ERROR_DELETING_PHOTO = "There was deleting the photo you selected. Please try again.";
    public static final String ERROR_UPLOADING_PHOTO = "There was an issue with the upload of your photo. Please try again.";
    private final VisitService visitService;
    private final VehicleMakeService vehicleMakeService;
    private final VehicleModelService vehicleModelService;
    private final ServicesService servicesService;
    private final PhotoService photoService;
    private final StripeService stripeServiceImpl;

    private final UserMapper userMapper;
    private final VehicleMapper vehicleMapper;
    private final VisitMapper visitMapper;
    private final ReportProducerHelper reportProducerHelper;
    private final AuthenticationHelper authenticationHelper;
    private final ForexCurrencyExchange currencyExchange;
    private final PaymentServices paymentService;
    private final PaymentRecordsService paymentRecordsService;
    private final PaymentRecordsMapper paymentRecordsMapper;
    private final SMSHelper smsHelper;

    @Autowired
    public VisitExtrasMVCController(VisitService visitService, VehicleMakeService vehicleMakeService,
                                    VehicleModelService vehicleModelService, ServicesService servicesService,
                                    PhotoService photoService, StripeService stripeServiceImpl,
                                    UserMapper userMapper, VehicleMapper vehicleMapper, VisitMapper visitMapper,
                                    ReportProducerHelper reportProducerHelper,
                                    AuthenticationHelper authenticationHelper,
                                    ForexCurrencyExchange currencyExchange,
                                    PaymentServices paymentService,
                                    PaymentRecordsService paymentRecordsService,
                                    PaymentRecordsMapper paymentRecordsMapper, SMSHelper smsHelper) {
        this.visitService = visitService;
        this.vehicleMakeService = vehicleMakeService;
        this.vehicleModelService = vehicleModelService;
        this.servicesService = servicesService;
        this.photoService = photoService;
        this.stripeServiceImpl = stripeServiceImpl;
        this.userMapper = userMapper;
        this.vehicleMapper = vehicleMapper;
        this.visitMapper = visitMapper;
        this.reportProducerHelper = reportProducerHelper;
        this.authenticationHelper = authenticationHelper;
        this.currencyExchange = currencyExchange;
        this.paymentService = paymentService;
        this.paymentRecordsService = paymentRecordsService;
        this.paymentRecordsMapper = paymentRecordsMapper;
        this.smsHelper = smsHelper;
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
        //TODO expires on the 25th May (we can get a new one just before presenting)

        //TODO our third free trial for FOREX expires on 2 May
        User currentUser = authenticationHelper.getCurrentUser();

        Currencies selectedCurrency = Enum.valueOf(Currencies.class, currency);

        try {
            reportProducerHelper.generateReport(currentUser, selectedVisits, selectedCurrency);
            return "visit-report-produced";
        } catch (IOException | EntityNotFoundException e) {
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
            model.addAttribute("errorMessage", ERROR_UPLOADING_PHOTO);
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
            model.addAttribute("errorMessage", ERROR_DELETING_PHOTO);
            return "not-found";
        }
    }


    @PostMapping("/{id}/pay-with-stripe")
    public String executeStripePayment(@PathVariable int id, ChargeRequest chargeRequest, Model model) throws StripeException {
        User currentUser = authenticationHelper.getCurrentUser();
        try {
            Visit visit = visitService.getById(currentUser, id);
            visitService.settle(currentUser, id);
            chargeRequest.setDescription("Repair services for: " + visit.getVehicle().getLicense() + " - " +
                    visit.getVehicle().makeName() + " " + visit.getVehicle().modelName());
            chargeRequest.setCurrency(Currencies.BGN);
            Charge charge = stripeServiceImpl.charge(chargeRequest);
            smsHelper.sendConfirmationSMS(visit, charge.getId());
            PaymentRecord paymentRecord = paymentRecordsMapper.toObject(visit, STRIPE, charge.getId());
            paymentRecordsService.create(currentUser, paymentRecord);
            model.addAttribute("visit", visit);
            model.addAttribute("chargeId", charge.getId());
            model.addAttribute("balance_transaction", charge.getBalanceTransaction());
            model.addAttribute("status", charge.getStatus());
        } catch (UnauthorizedOperationException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "unauthorised";
        } catch (EntityNotFoundException | IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        } catch (InvalidParameter e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/visits/payment-error";
        }
        return "stripe-success";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "visit";
    }

    @PostMapping("/{id}/pay-with-paypal")
    public void preparePaypalPayment(@PathVariable int id,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        User currentUser = authenticationHelper.getCurrentUser();

        try {
            Visit visit = visitService.getById(currentUser, id);
            double totalInUSD = currencyExchange.convertPriceFromBGNToForeignCurrency(Currencies.USD, visit.getTotalCost());
            PaypalOrder orderDetail = new PaypalOrder(String.valueOf(id), totalInUSD, 0, 0, totalInUSD);

            String approvalLink = paymentService.authorizePayment(orderDetail, currentUser, visit);

            response.sendRedirect(approvalLink);
        } catch (IOException | PayPalRESTException | UnauthorizedOperationException | EntityNotFoundException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            ex.printStackTrace();
            request.getRequestDispatcher("http://smartgarage.shop/visits/payment-error");
        }
    }

    @GetMapping("/{id}/pay-with-paypal/execute")
    public String executePaypalPayment(@PathVariable int id,
                                       HttpServletRequest request,
                                       Model model) {
        User currentUser = authenticationHelper.getCurrentUser();

        String paymentId = request.getParameter("paymentId");
        String payerId = request.getParameter("PayerID");

        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);
            Visit visit = visitService.getById(currentUser, id);

            visitService.settle(currentUser, id);
            PaymentRecord paymentRecord = paymentRecordsMapper.toObject(visit, PAYPAL, paymentId);
            paymentRecordsService.create(currentUser, paymentRecord);

            smsHelper.sendConfirmationSMS(visit, paymentId);

            return "redirect:/visits/payment-success";
        } catch (PayPalRESTException e) {
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
            return "unauthorised";
        } catch (IllegalStateException | ApiException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "not-found";
        }
    }

    @GetMapping("/payment-success")
    public String showSuccessfulPayment() {
        return "paypal-success";
    }

    @GetMapping("/payment-error")
    public String showPaypalPaymentError() {
        return "paypal-error";
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
