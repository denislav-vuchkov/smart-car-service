package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.models.VehicleModel;
import com.smart.garage.repositories.contracts.VehicleModelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class VehicleModelServiceImplTests {

    @Mock
    VehicleModelRepository vehicleModelRepository;

    @InjectMocks
    VehicleModelServiceImpl vehicleModelService;

    @Test
    void getAll_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.getAll(Helper.createCustomer()));
    }

    @Test
    void getAll_Should_Call_RepoMethod() {
        Mockito.when(vehicleModelRepository.getAll()).thenReturn(new ArrayList<>());
        vehicleModelService.getAll(Helper.createEmployee());
        Mockito.verify(vehicleModelRepository, Mockito.times(1)).getAll();
    }

    @Test
    void getById_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.getById(Helper.createCustomer(), 10));
    }

    @Test
    void getById_Should_Call_RepoMethod() {
        Mockito.when(vehicleModelRepository.getById(10)).thenReturn(new VehicleModel());
        vehicleModelService.getById(Helper.createEmployee(), 10);
        Mockito.verify(vehicleModelRepository, Mockito.times(1)).getById(10);
    }


    @Test
    void getByField_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.getByField(Helper.createCustomer(), "test", "value"));
    }

    @Test
    void getByField_Should_Call_RepoMethod() {
        Mockito.when(vehicleModelRepository.getByField("test", "value")).thenReturn(new VehicleModel());
        vehicleModelService.getByField(Helper.createEmployee(), "test", "value");
        Mockito.verify(vehicleModelRepository, Mockito.times(1))
                .getByField("test", "value");
    }

    @Test
    void create_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.create(Helper.createCustomer(), new VehicleModel()));
    }

    @Test
    void create_Throws_When_MakeIsNull() {
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleModelService.create(Helper.createEmployee(), new VehicleModel(0, "test", null)));
    }

    @Test
    void create_Should_Check_NameLength() {
        VehicleModel vehicleModel = new VehicleModel(0, "t", new VehicleMake(0, "test"));
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleModelService.create(Helper.createEmployee(), vehicleModel));
    }

    @Test
    void create_Should_Check_NameForDuplicate() {
        VehicleModel vehicleModel = new VehicleModel(0, "test", new VehicleMake(0, "test"));
        vehicleModelService.create(Helper.createEmployee(), vehicleModel);
        Mockito.verify(vehicleModelRepository, Mockito.times(1))
                .verifyFieldIsUnique(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void create_Should_Call_RepoMethod() {
        VehicleModel vehicleModel = new VehicleModel(0, "test", new VehicleMake(0, "test"));
        vehicleModelService.create(Helper.createEmployee(), vehicleModel);
        Mockito.verify(vehicleModelRepository, Mockito.times(1))
                .create(vehicleModel);
    }

    @Test
    void update_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.update(Helper.createCustomer(), new VehicleModel()));
    }

    @Test
    void update_Throws_When_MakeIsNull() {
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleModelService.create(Helper.createEmployee(), new VehicleModel(0, "test", null)));
    }

    @Test
    void update_Should_Check_NameLength() {
        VehicleModel vehicleModel = new VehicleModel(0, "t", new VehicleMake(0, "test"));
        Assertions.assertThrows(IllegalVehicleBrand.class,
                () -> vehicleModelService.update(Helper.createEmployee(), vehicleModel));
    }


    @Test
    void update_Should_Check_NameForDuplicate() {
        VehicleModel vehicleModel = new VehicleModel(0, "test", new VehicleMake(0, "test"));
        vehicleModelService.update(Helper.createEmployee(), vehicleModel);
        Mockito.verify(vehicleModelRepository, Mockito.times(1))
                .verifyFieldIsUnique(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void update_Should_Call_RepoMethod() {
        VehicleModel vehicleModel = new VehicleModel(0, "test", new VehicleMake(0, "test"));
        vehicleModelService.update(Helper.createEmployee(), vehicleModel);
        Mockito.verify(vehicleModelRepository, Mockito.times(1))
                .update(vehicleModel);
    }

    @Test
    void delete_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.delete(Helper.createCustomer(), 5));
    }

    @Test
    void delete_Should_Call_RepoMethod() {
        Mockito.when(vehicleModelRepository.getById(5)).thenReturn(new VehicleModel());
        vehicleModelService.delete(Helper.createEmployee(), 5);
        Mockito.verify(vehicleModelRepository, Mockito.times(1)).delete(Mockito.anyInt());
    }

    @Test
    void nameExists_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleModelService.nameExists(Helper.createCustomer(), "test"));
    }

    @Test
    void nameExists_ReturnsTrue_When_existingName() {
        Mockito.when(vehicleModelRepository.getByField(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new VehicleModel());
        Assertions.assertTrue(vehicleModelService.nameExists(Helper.createEmployee(), "test"));
    }

    @Test
    void nameExists_ReturnsFalse_When_newName() {
        Mockito.when(vehicleModelRepository.getByField(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(EntityNotFoundException.class);
        Assertions.assertFalse(vehicleModelService.nameExists(Helper.createEmployee(), "test"));
    }

}
