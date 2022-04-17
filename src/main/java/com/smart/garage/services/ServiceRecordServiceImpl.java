package com.smart.garage.services;

import com.smart.garage.models.ServiceRecord;
import com.smart.garage.models.User;
import com.smart.garage.repositories.contracts.ServiceRecordRepository;
import com.smart.garage.services.contracts.ServiceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.smart.garage.utility.AuthenticationHelper.validateUserIsEmployee;

@Service
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;

    @Autowired
    public ServiceRecordServiceImpl(ServiceRecordRepository serviceRecordRepository) {
        this.serviceRecordRepository = serviceRecordRepository;
    }

    @Override
    public List<ServiceRecord> getAll(User requester) {
        validateUserIsEmployee(requester);
        return serviceRecordRepository.getAll();
    }

    @Override
    public Set<ServiceRecord> getAll(User requester, int visitID) {
        validateUserIsEmployee(requester);
        return serviceRecordRepository.getAll(visitID);
    }

    @Override
    public ServiceRecord getById(User requester, int id) {
        validateUserIsEmployee(requester);
        return serviceRecordRepository.getById(id);
    }

    @Override
    public ServiceRecord create(User requester, ServiceRecord serviceRecord) {
        validateUserIsEmployee(requester);
        serviceRecordRepository.create(serviceRecord);
        return serviceRecord;
    }

    @Override
    public ServiceRecord update(User requester, ServiceRecord serviceRecord) {
        validateUserIsEmployee(requester);
        serviceRecordRepository.update(serviceRecord);
        return serviceRecord;
    }

    @Override
    public void delete(User requester, int id) {
        validateUserIsEmployee(requester);
        ServiceRecord serviceRecord = serviceRecordRepository.getById(id);
        serviceRecordRepository.delete(serviceRecord.getId());
    }
}
