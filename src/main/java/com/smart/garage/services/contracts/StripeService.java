package com.smart.garage.services.contracts;

import com.smart.garage.models.dtos.ChargeRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

public interface StripeService extends PaymentService {

    Charge charge(ChargeRequest chargeRequest) throws StripeException;
}
