package com.smart.garage.utility.mappers;

import com.smart.garage.models.PaymentRecord;
import com.smart.garage.models.Visit;
import org.springframework.stereotype.Component;

@Component
public class PaymentRecordsMapper {

    public PaymentRecord toObject(Visit visit, int paymentMethodId, String paymentId) {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setVisit(visit);
        paymentRecord.setPaymentMethod(paymentMethodId);
        paymentRecord.setPaymentId(paymentId);
        return paymentRecord;
    }

}
