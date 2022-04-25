package com.smart.garage.services;

import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.*;
import com.smart.garage.repositories.contracts.PaymentsRepository;
import com.smart.garage.services.contracts.PaymentRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.smart.garage.utility.AuthenticationHelper.RESTRICTED_FOR_OWNER;

@Service
public class PaymentRecordsServiceImpl implements PaymentRecordsService {

    public static final String VISIT_ALREADY_PAID = "The visit in question has already been paid for or not ready yet to be paid.";
    private final PaymentsRepository repository;

    @Autowired
    public PaymentRecordsServiceImpl(PaymentsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void create(User requester, PaymentRecord paymentRecord) {
        validateUser(requester, paymentRecord.getVisit());
        checkIfValidVisit(paymentRecord.getVisit());
        repository.create(paymentRecord);
    }

    private void checkIfValidVisit(Visit visit) {
        if (visit.getStatus().getId() != 5) {
            throw new InvalidParameter(VISIT_ALREADY_PAID);
        }
    }

    private void validateUser(User requester, Visit visit) {
        if (requester.getRole().getName() != UserRoles.CUSTOMER || requester.getId() != visit.getUser().getId()) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER);
        }
    }

}
