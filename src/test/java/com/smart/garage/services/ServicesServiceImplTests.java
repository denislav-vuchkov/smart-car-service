package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.repositories.contracts.ServicesRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ServicesServiceImplTests {

    @Mock
    ServicesRepository repository;

    @InjectMocks
    ServicesServiceImpl service;

    @Test
    void getAll_Should_CallRepository() {
        List<Servicez> servicezList = Helper.getListOfServices();

        Mockito.when(repository.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(servicezList);

        service.getAll(Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());

        Mockito.verify(repository, Mockito.times(1)).getAll(Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Test
    void getAll_Should_FilterResults() {
        List<Servicez> servicezList = new ArrayList<>();
        servicezList.add(Helper.createEngineService());

        Mockito.when(repository.getAll(Optional.of("engine"), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty())).thenReturn(servicezList);

        List<Servicez> result = service.getAll(Optional.of("engine"), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty());

        Assertions.assertEquals(servicezList, result);
    }

    @Test
    void getMostExpensiveService_Should_CallRepository() {
        Servicez expensiveService = Helper.createExpensiveService();
        Mockito.when(repository.getMostExpensiveService()).thenReturn(expensiveService);
        service.getMostExpensiveService();
        Mockito.verify(repository, Mockito.times(1)).getMostExpensiveService();
    }

    @Test
    void getById_Should_CallRepository() {
        Servicez dummyService = Helper.createFuelPumpService();
        Mockito.when(repository.getById(dummyService.getId())).thenReturn(dummyService);
        service.getById(dummyService.getId());
        Mockito.verify(repository, Mockito.times(1)).getById(dummyService.getId());
    }

    @Test
    void getById_Should_Throw_When_InvalidIDProvided() {
        Assertions.assertThrows(InvalidParameter.class, () -> service.getById(-20));
    }

    @Test
    void getById_Should_ReturnValidService_When_ValidInput() {
        Servicez dummyService = Helper.createFuelPumpService();
        Mockito.when(repository.getById(dummyService.getId())).thenReturn(dummyService);
        Servicez result = service.getById(dummyService.getId());
        Assertions.assertEquals(dummyService, result);
    }

    @Test
    void create_Should_CallRepository() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.doNothing().when(repository).verifyFieldIsUnique("name", dummyService.getName());
        Mockito.doNothing().when(repository).create(dummyService);
        service.create(requester, dummyService);
        Mockito.verify(repository, Mockito.times(1)).create(dummyService);
    }

    @Test
    void create_Should_Throw_When_RequesterIsCustomer() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.create(requester, dummyService));
    }

    @Test
    void create_Should_Throw_When_DuplicateName() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.doThrow(DuplicateEntityException.class).when(repository).verifyFieldIsUnique("name", dummyService.getName());
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.create(requester, dummyService));
    }

    @Test
    void create_Should_Execute_When_ValidInput() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.doNothing().when(repository).verifyFieldIsUnique("name", dummyService.getName());
        Mockito.doNothing().when(repository).create(dummyService);
        service.create(requester, dummyService);
        Assertions.assertDoesNotThrow(() -> service.create(requester, dummyService));
    }

    @Test
    void update_Should_CallRepository() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.when(repository.getBySpecificField("name", dummyService.getName())).thenReturn(dummyService);
        Mockito.doNothing().when(repository).update(dummyService);
        service.update(requester, dummyService);
        Mockito.verify(repository, Mockito.times(1)).update(dummyService);
    }

    @Test
    void update_Should_Throw_When_RequesterIsCustomer() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.update(requester, dummyService));
    }

    @Test
    void update_Should_Throw_When_DuplicateName() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.when(repository.getBySpecificField("name", dummyService.getName())).thenReturn(Helper.createEngineService());
        Assertions.assertThrows(DuplicateEntityException.class,
                () -> service.update(requester, dummyService));
    }

    @Test
    void update_Should_Execute_When_ValidInput() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.when(repository.getBySpecificField("name", dummyService.getName())).thenReturn(dummyService);
        Mockito.doNothing().when(repository).update(dummyService);
        service.update(requester, dummyService);
        Assertions.assertDoesNotThrow(() -> service.create(requester, dummyService));
    }



    @Test
    void delete_Should_CallRepository() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.when(repository.getById(dummyService.getId())).thenReturn(dummyService);
        Mockito.doNothing().when(repository).delete(dummyService.getId());
        service.delete(requester, dummyService.getId());
        Mockito.verify(repository, Mockito.times(1)).delete(dummyService.getId());
    }

    @Test
    void delete_Should_Throw_When_InvalidID() {
        User requester = Helper.createCustomer();
        Assertions.assertThrows(InvalidParameter.class,
                () -> service.delete(requester, -20));
    }

    @Test
    void delete_Should_Throw_When_RequesterIsCustomer() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> service.delete(requester, dummyService.getId()));
    }

    @Test
    void delete_Should_Execute_When_ValidInput() {
        Servicez dummyService = Helper.createFuelPumpService();
        User requester = Helper.createEmployee();
        Mockito.when(repository.getById(dummyService.getId())).thenReturn(dummyService);
        Mockito.doNothing().when(repository).delete(dummyService.getId());
        service.delete(requester, dummyService.getId());
        Assertions.assertDoesNotThrow(() -> service.delete(requester, dummyService.getId()));
    }



}
