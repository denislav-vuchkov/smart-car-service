package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.*;
import com.smart.garage.repositories.contracts.PaymentsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentRecordsServiceImplTests {

    @Mock
    PaymentsRepository repository;

    @InjectMocks
    PaymentRecordsServiceImpl service;

    @Test
    void create_Should_ThrowException_When_InvalidUser() {
        User requester = Helper.createEmployee();
        Visit visit = new Visit();
        visit.setUser(Helper.createCustomer());
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setVisit(visit);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.create(requester, paymentRecord));
    }

    @Test
    void create_Should_ThrowException_When_InvalidVisit() {
        User requester = Helper.createCustomer();
        Visit visit = new Visit();
        visit.setUser(requester);
        visit.setStatus(StatusCode.IN_PROGRESS.getStatus());
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setVisit(visit);

        Assertions.assertThrows(InvalidParameter.class,
                () -> service.create(requester, paymentRecord));
    }

    @Test
    void create_Should_CallRepository_When_ValidInput() {
        User requester = Helper.createCustomer();
        Visit visit = new Visit();
        visit.setUser(requester);
        visit.setStatus(StatusCode.READY_UNPAID.getStatus());
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setVisit(visit);

        service.create(requester, paymentRecord);

        Mockito.verify(repository, Mockito.times(1)).create(paymentRecord);
    }



}
