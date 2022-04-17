package com.smart.garage.controllers.rest;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.models.Currencies;
import com.smart.garage.models.User;
import com.smart.garage.models.dtos.ReportRequest;
import com.smart.garage.services.contracts.VisitService;
import com.smart.garage.utility.AuthenticationHelper;
import com.smart.garage.utility.ReportProducerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("api//visits")
public class VisitsController {

    private final AuthenticationHelper authenticationHelper;
    private final ReportProducerHelper reportProducerHelper;
    private final VisitService service;

    @Autowired
    public VisitsController(AuthenticationHelper authenticationHelper, ReportProducerHelper reportProducerHelper, VisitService service) {
        this.authenticationHelper = authenticationHelper;
        this.reportProducerHelper = reportProducerHelper;
        this.service = service;
    }

    @GetMapping("/generate-report")
    public void generatePDFReport(@Valid @RequestBody ReportRequest reportRequest) {
        User currentUser = authenticationHelper.getCurrentUser();

        Currencies selectedCurrency;
        try {
            selectedCurrency = Enum.valueOf(Currencies.class, reportRequest.getCurrency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a valid currency.");
        }

        try {
            reportProducerHelper.generateReport(currentUser, reportRequest.getVisitIDs(), selectedCurrency);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "There was an issue with the production of your report");
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (InvalidParameter e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
