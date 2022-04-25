package com.smart.garage.services.contracts;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.smart.garage.models.PaypalOrder;
import com.smart.garage.models.User;
import com.smart.garage.models.Visit;

import java.io.IOException;

public interface PaymentServices {

    String authorizePayment(PaypalOrder paypalOrder, User currentUser, Visit visit) throws PayPalRESTException, IOException;

    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;

}
