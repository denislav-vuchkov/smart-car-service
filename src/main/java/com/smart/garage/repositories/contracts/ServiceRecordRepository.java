package com.smart.garage.repositories.contracts;

import com.smart.garage.models.ServiceRecord;

import java.util.Set;

public interface ServiceRecordRepository extends BaseCRUDRepository<ServiceRecord> {

    Set<ServiceRecord> getAll(int visitID);

}