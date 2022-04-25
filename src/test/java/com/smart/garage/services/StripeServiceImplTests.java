package com.smart.garage.services;

import com.smart.garage.models.dtos.ChargeRequest;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StripeServiceImplTests {

    @InjectMocks
    StripeServiceImpl stripeService;

    @Test
    void charge_throws_when_not_initializedWithKey() {
        Assertions.assertThrows(StripeException.class, () -> stripeService.charge(new ChargeRequest()));
    }

    @Test
    void charge_throws_when_chargeRequestNotValid() {
        stripeService.init();
        Assertions.assertThrows(InvalidRequestException.class, () -> stripeService.charge(new ChargeRequest()));
    }
}
