package com.smart.garage.services;

import com.smart.garage.models.dtos.ChargeRequest;
import com.smart.garage.services.contracts.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private String secretKey = "sk_test_51KqWsVJk66hxMyhp6F0Wx3yhl1h25FO0MDAg1T1nYR3xnp9L2VkAvy3AI65BKDaC4WVmis9hmGD1OFplNrb72Vwm00ItYUvDMX";

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public Charge charge(ChargeRequest chargeRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("description", chargeRequest.getDescription());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return Charge.create(chargeParams);
    }
}