package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.ServiceRecord;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.Visit;
import com.smart.garage.repositories.contracts.ServiceRecordRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;

@ExtendWith(MockitoExtension.class)
public class ServiceRecordServiceImplTests {

    @Mock
    ServiceRecordRepository serviceRecordRepository;

    @InjectMocks
    ServiceRecordServiceImpl serviceRecordService;

    @Test
    void getAll_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.getAll(Helper.createCustomer()));
    }

    @Test
    void getAll_Should_Call_RepoMethod() {
        Mockito.when(serviceRecordRepository.getAll()).thenReturn(new ArrayList<>());
        serviceRecordService.getAll(Helper.createEmployee());
        Mockito.verify(serviceRecordRepository, Mockito.times(1)).getAll();
    }

    @Test
    void getByAllOverloaded_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.getAll(Helper.createCustomer(), 1));
    }

    @Test
    void getByAllOverloaded_Should_Call_RepoMethod() {
        serviceRecordService.getAll(Helper.createEmployee(), 1);
        Mockito.verify(serviceRecordRepository, Mockito.times(1))
                .getAll(1);
    }

    @Test
    void getById_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.getById(Helper.createCustomer(), 10));
    }

    @Test
    void getById_Should_Call_RepoMethod() {
        Mockito.when(serviceRecordRepository.getById(10)).thenReturn(new ServiceRecord());
        serviceRecordService.getById(Helper.createEmployee(), 10);
        Mockito.verify(serviceRecordRepository, Mockito.times(1)).getById(10);
    }

    @Test
    void create_Throws_When_UserIsNotEmployee() {

        User customer1 = Helper.createCustomer();
        customer1.setVehicleSet(new HashSet<>());
        customer1.setId(5);

        User customer2 = Helper.createCustomer();
        customer2.setVehicleSet(new HashSet<>());
        customer2.setId(10);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer2);

        Visit visit = new Visit();
        visit.setUser(customer2);
        visit.setVehicle(vehicle);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.create(customer1, new ServiceRecord(), visit));
    }

    @Test
    void create_Should_Call_RepoMethod() {
        ServiceRecord serviceRecord = new ServiceRecord();
        serviceRecordService.create(Helper.createEmployee(), serviceRecord, new Visit());
        Mockito.verify(serviceRecordRepository, Mockito.times(1))
                .create(serviceRecord);
    }

    @Test
    void update_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.update(Helper.createCustomer(), new ServiceRecord()));
    }

    @Test
    void update_Should_Call_RepoMethod() {
        ServiceRecord serviceRecord = new ServiceRecord();
        serviceRecordService.update(Helper.createEmployee(), serviceRecord);
        Mockito.verify(serviceRecordRepository, Mockito.times(1))
                .update(serviceRecord);
    }

    @Test
    void delete_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> serviceRecordService.delete(Helper.createCustomer(), 5));
    }

    @Test
    void delete_Should_Call_RepoMethod() {
        Mockito.when(serviceRecordRepository.getById(5)).thenReturn(new ServiceRecord());
        serviceRecordService.delete(Helper.createEmployee(), 5);
        Mockito.verify(serviceRecordRepository, Mockito.times(1)).delete(Mockito.anyInt());
    }
}
