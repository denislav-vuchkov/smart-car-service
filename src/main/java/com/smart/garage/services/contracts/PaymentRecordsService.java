package com.smart.garage.services.contracts;

import com.smart.garage.models.PaymentRecord;
import com.smart.garage.models.User;

public interface PaymentRecordsService {
    void create(User requester, PaymentRecord paymentRecord);
}
