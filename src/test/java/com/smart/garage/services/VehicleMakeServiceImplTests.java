package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.models.VehicleModel;
import com.smart.garage.repositories.contracts.VehicleMakeRepository;
import com.smart.garage.repositories.contracts.VehicleRepository;
import com.smart.garage.services.contracts.VehicleMakeService;
import com.smart.garage.services.contracts.VehicleModelService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class VehicleMakeServiceImplTests {

    @Mock
    VehicleMakeRepository vehicleMakeRepository;

    @InjectMocks
    VehicleMakeServiceImpl vehicleMakeService;

    @Test
    void getAll_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.getAll(Helper.createCustomer()));
    }

    @Test
    void getAll_Should_Call_RepoMethod() {
        Mockito.when(vehicleMakeRepository.getAll()).thenReturn(new ArrayList<>());
        vehicleMakeService.getAll(Helper.createEmployee());
        Mockito.verify(vehicleMakeRepository, Mockito.times(1)).getAll();
    }

    @Test
    void getById_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.getById(Helper.createCustomer(), 10));
    }

    @Test
    void getById_Should_Call_RepoMethod() {
        Mockito.when(vehicleMakeRepository.getById(10)).thenReturn(new VehicleMake());
        vehicleMakeService.getById(Helper.createEmployee(), 10);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1)).getById(10);
    }


    @Test
    void getByField_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.getByField(Helper.createCustomer(), "test", "value"));
    }

    @Test
    void getByField_Should_Call_RepoMethod() {
        Mockito.when(vehicleMakeRepository.getByField("test", "value")).thenReturn(new VehicleMake());
        vehicleMakeService.getByField(Helper.createEmployee(), "test", "value");
        Mockito.verify(vehicleMakeRepository, Mockito.times(1))
                .getByField("test", "value");
    }

    @Test
    void create_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.create(Helper.createCustomer(), new VehicleMake()));
    }

    @Test
    void create_Should_Check_NameLength() {
        VehicleMake vehicleMake = new VehicleMake(0, "t");
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleMakeService.create(Helper.createEmployee(), vehicleMake));
    }


    @Test
    void create_Should_Check_NameForDuplicate() {
        VehicleMake vehicleMake = new VehicleMake(0, "test");
        vehicleMakeService.create(Helper.createEmployee(), vehicleMake);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1))
                .verifyFieldIsUnique(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void create_Should_Call_RepoMethod() {
        VehicleMake vehicleMake = new VehicleMake(0, "test");
        vehicleMakeService.create(Helper.createEmployee(), vehicleMake);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1))
                .create(vehicleMake);
    }

    @Test
    void update_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.update(Helper.createCustomer(), new VehicleMake()));
    }

    @Test
    void update_Should_Check_NameLength() {
        VehicleMake vehicleMake = new VehicleMake(0, "t");
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleMakeService.update(Helper.createEmployee(), vehicleMake));
    }


    @Test
    void update_Should_Check_NameForDuplicate() {
        VehicleMake vehicleMake = new VehicleMake(0, "test");
        vehicleMakeService.update(Helper.createEmployee(), vehicleMake);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1))
                .verifyFieldIsUnique(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void update_Should_Call_RepoMethod() {
        VehicleMake vehicleMake = new VehicleMake(0, "test");
        vehicleMakeService.update(Helper.createEmployee(), vehicleMake);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1))
                .update(vehicleMake);
    }

    @Test
    void delete_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.delete(Helper.createCustomer(), 5));
    }

    @Test
    void delete_Should_Call_RepoMethod() {
        Mockito.when(vehicleMakeRepository.getById(5)).thenReturn(new VehicleMake());
        vehicleMakeService.delete(Helper.createEmployee(), 5);
        Mockito.verify(vehicleMakeRepository, Mockito.times(1)).delete(Mockito.anyInt());
    }

    @Test
    void nameExists_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleMakeService.nameExists(Helper.createCustomer(), "test"));
    }

    @Test
    void nameExists_ReturnsTrue_When_existingName() {
        Mockito.when(vehicleMakeRepository.getByField(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new VehicleMake());
        Assertions.assertTrue(vehicleMakeService.nameExists(Helper.createEmployee(), "test"));
    }

    @Test
    void nameExists_ReturnsFalse_When_newName() {
        Mockito.when(vehicleMakeRepository.getByField(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);
        Assertions.assertFalse(vehicleMakeService.nameExists(Helper.createEmployee(), "test"));
    }

}
