package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.IllegalVehicleBrand;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.models.VehicleMake;
import com.smart.garage.models.VehicleModel;
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
public class VehicleServiceImplTests {

    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    VehicleMakeService vehicleMakeService;
    @Mock
    VehicleModelService vehicleModelService;

    @InjectMocks
    VehicleServiceImpl vehicleService;

    @Test
    void getAllMVC_Throws_When_UserIsNotEmployeeAndAccessingAllVehicles() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.getAll(Helper.createCustomer(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true));
    }

    @Test
    void getAllMVC_Returns_When_UserIsLookingAtOwnVehicles() {
        Mockito.when(vehicleRepository.getAll(
                        Optional.empty(), Optional.of(Set.of(Helper.createEmployee().getId())),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        vehicleService.getAll(Helper.createEmployee(),
                Optional.empty(), Optional.of(Set.of(Helper.createEmployee().getId())),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);

        Mockito.verify(vehicleRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.of(Set.of(Helper.createEmployee().getId())),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getAllMVC_Should_Call_RepoMethod() {
        Mockito.when(vehicleRepository.getAll(
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        vehicleService.getAll(Helper.createEmployee(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);

        Mockito.verify(vehicleRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getAllREST_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.getAll(Helper.createCustomer(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true));
    }

    @Test
    void getAllREST_Should_Call_RepoMethod() {
        Mockito.when(vehicleRepository.getAll(
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        vehicleService.getAll(Helper.createEmployee(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);

        Mockito.verify(vehicleRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getAllNoFilters_Should_Call_OverloadedMethod() {
        Mockito.when(vehicleRepository.getAll(
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        vehicleService.getAll(Helper.createEmployee(), true);

        Mockito.verify(vehicleRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getById_Throws_When_UserIsNotEmployeeAndAccessingForeignVehicle() {
        User customer1 = Helper.createCustomer();
        customer1.setId(5);
        User customer2 = Helper.createCustomer();
        customer2.setId(10);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer2);
        Mockito.when(vehicleRepository.getById(10)).thenReturn(vehicle);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.getById(customer1, 10));
    }

    @Test
    void getById_Returns_When_UserIsLookingAtOwnVehicle() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Mockito.when(vehicleRepository.getById(5))
                .thenReturn(vehicle);

        vehicleService.getById(customer, 5);
        Mockito.verify(vehicleRepository, Mockito.times(1)).getById(5);
    }

    @Test
    void getById_Should_Call_RepoMethod() {
        Mockito.when(vehicleRepository.getById(10)).thenReturn(new Vehicle());
        vehicleService.getById(Helper.createEmployee(), 10);
        Mockito.verify(vehicleRepository, Mockito.times(1)).getById(10);
    }

    @Test
    void create_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.create(Helper.createCustomer(), new Vehicle()));
    }

    @Test
    void create_Should_Check_VinForDuplicate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                verifyFieldIsUnique("VIN", "testVIN");
    }

    @Test
    void create_Should_Check_LicenseForDuplicate() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                verifyFieldIsUnique("license", "testLicense");
    }

    @Test
    void create_Should_Call_RepoMethod() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                create(vehicle);
    }

    @Test
    void update_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.update(Helper.createCustomer(), new Vehicle()));
    }

    @Test
    void update_Should_Retrieve_OriginalFromRepo() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setId(5);
        vehicle1.setLicense("testLicense");
        vehicle1.setVIN("testVIN");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicense("differentLicense");
        vehicle2.setVIN("differentVIN");

        vehicle1.setModel(new VehicleModel(0, "testModel",
                new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle2);

        vehicleService.update(Helper.createEmployee(), vehicle1);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                getById(vehicle1.getId());
    }

    @Test
    void update_Should_Check_VinForDuplicate() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicense("testLicense");
        vehicle1.setVIN("testVIN");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicense("differentLicense");
        vehicle2.setVIN("differentVIN");

        vehicle1.setModel(new VehicleModel(0, "testModel",
                new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle2);

        vehicleService.update(Helper.createEmployee(), vehicle1);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                verifyFieldIsUnique("VIN", "testVIN");
    }

    @Test
    void update_Should_Check_LicenseForDuplicate() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicense("testLicense");
        vehicle1.setVIN("testVIN");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicense("differentLicense");
        vehicle2.setVIN("differentVIN");

        vehicle1.setModel(new VehicleModel(0, "testModel",
                new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle2);

        vehicleService.update(Helper.createEmployee(), vehicle1);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                verifyFieldIsUnique("license", "testLicense");
    }

    @Test
    void update_Should_Call_RepoMethod() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setId(5);
        vehicle1.setLicense("testLicense");
        vehicle1.setVIN("testVIN");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicense("differentLicense");
        vehicle2.setVIN("differentVIN");

        vehicle1.setModel(new VehicleModel(0, "testModel",
                new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleModel());
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle2);

        vehicleService.update(Helper.createEmployee(), vehicle1);
        Mockito.verify(vehicleRepository, Mockito.times(1)).
                update(vehicle1);
    }

    @Test
    void delete_Throw_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> vehicleService.softDelete(Helper.createCustomer(), 5));
    }

    @Test
    void delete_Should_Retrieve_OriginalFromRepo() {
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(new Vehicle());
        vehicleService.softDelete(Helper.createEmployee(), 10);
        Mockito.verify(vehicleRepository, Mockito.times(1)).getById(10);
    }

    @Test
    void delete_Throws_When_AlreadyDeleted() {
        Vehicle vehicle = new Vehicle();
        vehicle.setDeleted(true);
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle);
        Assertions.assertThrows(DuplicateEntityException.class, () ->
                vehicleService.softDelete(Helper.createEmployee(), 10));
    }

    @Test
    void delete_Should_Update_VehicleStatus() {
        Vehicle vehicle = new Vehicle();
        vehicle.setDeleted(false);
        Mockito.when(vehicleRepository.getById(Mockito.anyInt())).thenReturn(vehicle);
        vehicleService.softDelete(Helper.createEmployee(), 10);
        Assertions.assertTrue(vehicle.isDeleted());
        Mockito.verify(vehicleRepository, Mockito.times(1)).update(vehicle);
    }

    @Test
    void verifyFieldIsUnique_Throws_When_UserNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class, () ->
                vehicleService.softDelete(Helper.createCustomer(), 10));
    }

    @Test
    void verifyFieldIsUnique_Should_Call_RepoMethod() {
        vehicleService.verifyFieldIsUnique(Helper.createEmployee(), "test", "value");
        Mockito.verify(vehicleRepository, Mockito.times(1))
                .verifyFieldIsUnique("test", "value");
    }

    @Test
    void getModel_Should_SetModelFromRepo_IfNameExists() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel", new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleModelService, Mockito.times(1)).
                getByField(Mockito.any(User.class), Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    void getModel_Should_Create_Model_If_MakeExistButModelNew() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(true);
        Mockito.when(vehicleModelService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(false);
        Mockito.when(vehicleMakeService.getByField(Mockito.any(User.class), Mockito.any(String.class),
                Mockito.any(String.class))).thenReturn(new VehicleMake());

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleModelService, Mockito.times(1)).
                create(Mockito.any(User.class), Mockito.any(VehicleModel.class));
    }

    @Test
    void getModel_Should_Create_MakeAndModel_If_BothNew() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(false);
        Mockito.when(vehicleMakeService.create(Mockito.any(User.class), Mockito.any(VehicleMake.class)))
                .thenReturn(new VehicleMake());
        Mockito.when(vehicleModelService.create(Mockito.any(User.class), Mockito.any(VehicleModel.class)))
                .thenReturn(new VehicleModel());

        vehicleService.create(Helper.createEmployee(), vehicle);
        Mockito.verify(vehicleMakeService, Mockito.times(1)).
                create(Mockito.any(User.class), Mockito.any(VehicleMake.class));
        Mockito.verify(vehicleModelService, Mockito.times(1)).
                create(Mockito.any(User.class), Mockito.any(VehicleModel.class));
    }

    @Test
    void getModel_Should_Delete_NewMake_If_ErrorOccursWithNewModel() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVIN("testVIN");
        vehicle.setModel(new VehicleModel(0, "testModel"
                , new VehicleMake(0, "testMake")));

        Mockito.when(vehicleMakeService.nameExists(
                Mockito.any(User.class), Mockito.any(String.class))).thenReturn(false);
        Mockito.when(vehicleMakeService.create(Mockito.any(User.class), Mockito.any(VehicleMake.class)))
                .thenReturn(new VehicleMake());
        Mockito.when(vehicleModelService.create(Mockito.any(User.class), Mockito.any(VehicleModel.class)))
                .thenThrow(IllegalVehicleBrand.class);

        Assertions.assertThrows(IllegalVehicleBrand.class, () -> vehicleService.create(Helper.createEmployee(), vehicle));
        Mockito.verify(vehicleMakeService, Mockito.times(1)).
                create(Mockito.any(User.class), Mockito.any(VehicleMake.class));
        Mockito.verify(vehicleMakeService, Mockito.times(1)).
                delete(Mockito.any(User.class), Mockito.anyInt());

    }
}
