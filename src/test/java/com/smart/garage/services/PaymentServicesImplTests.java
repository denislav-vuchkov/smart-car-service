package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.models.*;
import com.smart.garage.utility.ForexCurrencyExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PaymentServicesImplTests {

    @Mock
    ForexCurrencyExchange currencyExchange;

    @InjectMocks
    PaymentServicesImpl service;

    @Test
    void authorizePayment_Should_Execute_When_ValidInput() throws IOException {
        PaypalOrder paypalOrder = new PaypalOrder("38", 400, 0, 0, 400);
        User requester = Helper.createCustomer();
        Visit visit = new Visit();
        visit.setId(38);
        Vehicle vehicle = new Vehicle(1, requester, "CA0000TD", "321321321321", 2000, new VehicleModel(), false);
        visit.setVehicle(vehicle);
        Set<ServiceRecord> serviceRecordsList = new HashSet<>();
        serviceRecordsList.add(new ServiceRecord(1, 38, 1, "Engine", 300));
        serviceRecordsList.add(new ServiceRecord(2, 38, 2, "Tires", 150));
        visit.setServices(serviceRecordsList);

        Mockito.when(currencyExchange.convertPriceFromBGNToForeignCurrency(Mockito.any(Currencies.class), Mockito.anyInt()))
                .thenReturn(200.0);

        Assertions.assertDoesNotThrow(() -> service.authorizePayment(paypalOrder, requester, visit));
    }

}
