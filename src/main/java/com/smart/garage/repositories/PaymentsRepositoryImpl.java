package com.smart.garage.repositories;

import com.smart.garage.models.PaymentRecord;
import com.smart.garage.repositories.contracts.PaymentsRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepositoryImpl extends AbstractCRUDRepository<PaymentRecord> implements PaymentsRepository {

    public PaymentsRepositoryImpl() {
        super(PaymentRecord.class);
    }

}
