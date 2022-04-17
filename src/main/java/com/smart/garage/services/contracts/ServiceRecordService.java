package com.smart.garage.services.contracts;

import com.smart.garage.models.ServiceRecord;
import com.smart.garage.models.User;

import java.util.List;
import java.util.Set;

public interface ServiceRecordService {

    List<ServiceRecord> getAll(User requester);

    Set<ServiceRecord> getAll(User requester, int visitID);

    ServiceRecord getById(User requester, int id);

    ServiceRecord create(User requester, ServiceRecord serviceRecord);

    ServiceRecord update(User requester, ServiceRecord serviceRecord);

    void delete(User requester, int id);

}
