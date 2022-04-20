package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.*;
import com.smart.garage.repositories.contracts.VisitRepository;
import com.smart.garage.services.contracts.EmailService;
import com.smart.garage.services.contracts.ServiceRecordService;
import com.smart.garage.services.contracts.ServicesService;
import com.smart.garage.services.contracts.VehicleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class VisitServiceImplTests {

    @Mock
    VisitRepository visitRepository;
    @Mock
    UserServiceImpl userService;
    @Mock
    VehicleService vehicleService;
    @Mock
    ServicesService servicesService;
    @Mock
    ServiceRecordService serviceRecordService;
    @Mock
    EmailService emailService;

    @InjectMocks
    VisitServiceImpl visitService;

    @Test
    void getAll_Throws_When_UserIsNotEmployeeAndAccessingAllVisits() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.getAll(Helper.createCustomer(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true));
    }

    @Test
    void getAll_Returns_When_UserIsLookingAtOwnVisits() {
        User customer = Helper.createCustomer();
        Mockito.when(visitRepository.getAll(Optional.of(Set.of(customer.getId())), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        visitService.getAll(customer, Optional.of(Set.of(customer.getId())), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);

        Mockito.verify(visitRepository, Mockito.times(1)).getAll(
                Optional.of(Set.of(customer.getId())), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getAll_Should_Call_RepoMethod() {
        Mockito.when(visitRepository.getAll(Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        visitService.getAll(Helper.createEmployee(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);

        Mockito.verify(visitRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getAllNoFilters_Should_Call_OverloadedMethod() {
        User employee = Helper.createEmployee();
        Mockito.when(visitRepository.getAll(
                        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), true))
                .thenReturn(new ArrayList<>());

        visitService.getAll(employee, true);

        Mockito.verify(visitRepository, Mockito.times(1)).getAll(
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), true);
    }

    @Test
    void getById_Throws_When_UserIsNotEmployeeAndAccessingForeignVisit() {
        User customer1 = Helper.createCustomer();
        customer1.setId(5);
        User customer2 = Helper.createCustomer();
        customer2.setId(10);
        Visit visit = new Visit();
        visit.setUser(customer2);
        Mockito.when(visitRepository.getById(10)).thenReturn(visit);
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.getById(customer1, 10));
    }

    @Test
    void getById_Returns_When_UserIsLookingAtOwnVisit() {
        User customer = Helper.createCustomer();
        Visit visit = new Visit();
        visit.setUser(customer);
        Mockito.when(visitRepository.getById(5)).thenReturn(visit);
        visitService.getById(customer, 5);
        Mockito.verify(visitRepository, Mockito.times(1)).getById(5);
    }

    @Test
    void getById_Should_Call_RepoMethod() {
        Mockito.when(visitRepository.getById(10)).thenReturn(new Visit());
        visitService.getById(Helper.createEmployee(), 10);
        Mockito.verify(visitRepository, Mockito.times(1)).getById(10);
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
                () -> visitService.create(customer1, visit, new HashSet<>()));
    }

    @Test
    void create_Should_Check_IfVehicleAndOwnerMatch() {
        User customer1 = Helper.createCustomer();
        customer1.setId(5);
        Visit visit = new Visit();
        visit.setUser(customer1);
        User customer2 = Helper.createCustomer();
        customer2.setId(10);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer2);
        visit.setVehicle(vehicle);

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer1);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Assertions.assertThrows(InvalidParameter.class,
                () -> visitService.create(Helper.createEmployee(), visit, new HashSet<>()));
    }

    @Test
    void create_Should_Check_IfDatesChronological() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now().plusDays(1));
        visit.setEndDate(LocalDate.now());

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Assertions.assertThrows(InvalidParameter.class,
                () -> visitService.create(Helper.createEmployee(), visit, new HashSet<>()));
    }

    @Test
    void create_Should_Map_ServicesFromIds() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setServices(new HashSet<>());
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        Servicez testService = new Servicez(0, "test", 50);

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(servicesService.getById(Mockito.anyInt())).thenReturn(testService);
        visitService.create(Helper.createEmployee(), visit, Set.of(1, 2, 3));
        Mockito.verify(servicesService, Mockito.times(3)).getById(Mockito.anyInt());
    }

    @Test
    void create_Should_Save_ServiceAsRecords() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setServices(new HashSet<>());
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        Servicez testService = new Servicez(0, "test", 50);

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(servicesService.getById(Mockito.anyInt())).thenReturn(testService);
        visitService.create(Helper.createEmployee(), visit, Set.of(7));
        Mockito.verify(serviceRecordService, Mockito.times(1))
                .create(Mockito.any(User.class), Mockito.any(ServiceRecord.class), Mockito.any(Visit.class));
    }

    @Test
    void create_Should_Call_RepoMethod() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setServices(new HashSet<>());
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        visitService.create(Helper.createEmployee(), visit, new HashSet<>());
        Mockito.verify(visitRepository, Mockito.times(1)).create(visit);
    }


    @Test
    void createOverloaded_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.create(Helper.createCustomer(), new User(), new Vehicle(), new Visit(), new HashSet<>()));
    }

    @Test
    void createOverloaded_Should_Check_UsernameForDuplicate() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(Helper.createEmployee(), customer, vehicle, visit, new HashSet<>());
        Mockito.verify(userService, Mockito.times(1))
                .verifyFieldIsUnique("username", customer.getUsername());
    }

    @Test
    void createOverloaded_Should_Check_EmailForDuplicate() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(Helper.createEmployee(), customer, vehicle, visit, new HashSet<>());
        Mockito.verify(userService, Mockito.times(1))
                .verifyFieldIsUnique("email", customer.getEmail());
    }

    @Test
    void createOverloaded_Should_Check_PhoneForDuplicate() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(Helper.createEmployee(), customer, vehicle, visit, new HashSet<>());
        Mockito.verify(userService, Mockito.times(1))
                .verifyFieldIsUnique("phoneNumber", customer.getPhoneNumber());
    }

    @Test
    void createOverloaded_Should_Check_LicenseForDuplicate() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        User employee = Helper.createEmployee();

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(employee, customer, vehicle, visit, new HashSet<>());
        Mockito.verify(vehicleService, Mockito.times(1))
                .verifyFieldIsUnique(employee, "license", vehicle.getLicense());
    }

    @Test
    void createOverloaded_Should_Set_UserAndVehicle() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        User employee = Helper.createEmployee();

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(employee, customer, vehicle, visit, new HashSet<>());
        Assertions.assertEquals(visit.getUser(), customer);
        Assertions.assertEquals(visit.getVehicle(), vehicle);
    }

    @Test
    void createOverloaded_Should_Check_VinForDuplicate() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        User employee = Helper.createEmployee();

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(userService.create(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(customer);
        Mockito.when(vehicleService.create(Mockito.any(User.class), Mockito.any(Vehicle.class))).thenReturn(vehicle);

        visitService.create(employee, customer, vehicle, visit, new HashSet<>());
        Mockito.verify(vehicleService, Mockito.times(1))
                .verifyFieldIsUnique(employee, "VIN", vehicle.getVIN());
    }

    @Test
    void createOverloaded_Should_Check_IfDatesChronological() {
        User customer = Helper.createCustomer();
        Vehicle vehicle = new Vehicle();
        vehicle.setLicense("testLicense");
        vehicle.setVIN("testVIN");
        Visit visit = new Visit();
        visit.setStartDate(LocalDate.now().plusDays(1));
        visit.setEndDate(LocalDate.now());
        Assertions.assertThrows(InvalidParameter.class,
                () -> visitService.create(Helper.createEmployee(), customer, vehicle, visit, new HashSet<>()));
    }

    @Test
    void accept_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.accept(Helper.createCustomer(), 5));
    }

    @Test
    void accept_Should_Retrieve_OriginalFromRepo() {
        Visit visit = new Visit();
        visit.setId(10);
        visit.setUser(Helper.createCustomer());
        visit.setVehicle(new Vehicle());
        visit.setStartDate(LocalDate.now());
        visit.setStatus(StatusCode.REQUESTED.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Mockito.when(emailService.buildVisitConfirmationEmail(
                Mockito.any(Vehicle.class), Mockito.any(LocalDate.class))).thenReturn("");
        visitService.accept(Helper.createEmployee(), 10);
        Mockito.verify(visitRepository, Mockito.times(1)).getById(10);
    }

    @Test
    void accept_Throws_When_StatusNotRequested() {
        Visit visit = new Visit();
        visit.setStatus(StatusCode.IN_PROGRESS.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Assertions.assertThrows(IllegalStateException.class,
                () -> visitService.accept(Helper.createEmployee(), 5));
    }

    @Test
    void accept_Should_ChangeStatus_FromRequestedToNotStarted() {
        Visit visit = new Visit();
        visit.setId(10);
        visit.setUser(Helper.createCustomer());
        visit.setVehicle(new Vehicle());
        visit.setStartDate(LocalDate.now());
        visit.setStatus(StatusCode.REQUESTED.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Mockito.when(emailService.buildVisitConfirmationEmail(
                Mockito.any(Vehicle.class), Mockito.any(LocalDate.class))).thenReturn("");
        visitService.accept(Helper.createEmployee(), 10);
        Assertions.assertEquals(StatusCode.NOT_STARTED.getStatus(), visit.getStatus());
    }

    @Test
    void accept_Should_Call_UpdateRepoMethod() {
        Visit visit = new Visit();
        visit.setId(10);
        visit.setUser(Helper.createCustomer());
        visit.setVehicle(new Vehicle());
        visit.setStartDate(LocalDate.now());
        visit.setStatus(StatusCode.REQUESTED.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Mockito.when(emailService.buildVisitConfirmationEmail(
                Mockito.any(Vehicle.class), Mockito.any(LocalDate.class))).thenReturn("");
        visitService.accept(Helper.createEmployee(), 10);
        Mockito.verify(visitRepository, Mockito.times(1)).update(visit);
    }


    @Test
    void accept_Should_Call_SendEmailMethod() {
        Visit visit = new Visit();
        visit.setId(10);
        visit.setUser(Helper.createCustomer());
        visit.setVehicle(new Vehicle());
        visit.setStartDate(LocalDate.now());
        visit.setStatus(StatusCode.REQUESTED.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Mockito.when(emailService.buildVisitConfirmationEmail(
                Mockito.any(Vehicle.class), Mockito.any(LocalDate.class))).thenReturn("");
        visitService.accept(Helper.createEmployee(), 10);
        Mockito.verify(emailService, Mockito.times(1)).send(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.nullable(ByteArrayOutputStream.class), Mockito.nullable(String.class));
    }


    @Test
    void update_Throws_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.update(Helper.createCustomer(), new Visit(), new HashSet<>()));
    }

    @Test
    void update_Should_Check_IfVehicleAndOwnerMatch() {
        User customer1 = Helper.createCustomer();
        customer1.setId(5);
        Visit visit = new Visit();
        visit.setUser(customer1);
        User customer2 = Helper.createCustomer();
        customer2.setId(10);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer2);
        visit.setVehicle(vehicle);

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer1);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Assertions.assertThrows(InvalidParameter.class,
                () -> visitService.update(Helper.createEmployee(), visit, new HashSet<>()));
    }

    @Test
    void update_Should_Check_IfDatesChronological() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now().plusDays(1));
        visit.setEndDate(LocalDate.now());

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Assertions.assertThrows(InvalidParameter.class,
                () -> visitService.update(Helper.createEmployee(), visit, new HashSet<>()));
    }


    @Test
    void update_Should_Map_ServicesFromIds() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setServices(new HashSet<>());
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));
        Servicez testService = new Servicez(0, "test", 50);

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Mockito.when(servicesService.getById(Mockito.anyInt())).thenReturn(testService);
        visitService.update(Helper.createEmployee(), visit, Set.of(1, 2, 3, 4));
        Mockito.verify(servicesService, Mockito.times(4)).getById(Mockito.anyInt());
    }

    @Test
    void update_Should_Skip_ServicesRecords_IfUnchanged() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);

        Visit visit1 = new Visit();
        visit1.setId(10);
        visit1.setServices(new HashSet<>());
        visit1.setUser(customer);
        visit1.setVehicle(vehicle);
        visit1.setStartDate(LocalDate.now());
        visit1.setEndDate(LocalDate.now().plusDays(1));
        Servicez testService1 = new Servicez(1, "test1", 51);

        Visit visit2 = new Visit();
        visit2.setId(10);
        visit2.setServices(new HashSet<>());
        visit2.setUser(customer);
        visit2.setVehicle(vehicle);
        visit2.setStartDate(LocalDate.now());
        visit2.setEndDate(LocalDate.now().plusDays(1));
        visit2.setServices(Set.of(
                new ServiceRecord(0, visit2.getId(), 1, "test1", 51)));

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit2);
        Mockito.when(servicesService.getById(Mockito.anyInt())).thenReturn(testService1);
        visitService.update(Helper.createEmployee(), visit1, Set.of(1, 2, 3));
        Mockito.verify(serviceRecordService, Mockito.times(0))
                .delete(Mockito.any(User.class), Mockito.anyInt());
        Mockito.verify(serviceRecordService, Mockito.times(0))
                .create(Mockito.any(User.class), Mockito.any(ServiceRecord.class), Mockito.any(Visit.class));
    }

    @Test
    void update_Should_Replace_ServiceAsRecords_IfChanged() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);

        Visit visit1 = new Visit();
        visit1.setId(10);
        visit1.setServices(new HashSet<>());
        visit1.setUser(customer);
        visit1.setVehicle(vehicle);
        visit1.setStartDate(LocalDate.now());
        visit1.setEndDate(LocalDate.now().plusDays(1));
        Servicez testService1 = new Servicez(1, "test1", 51);

        Visit visit2 = new Visit();
        visit2.setId(10);
        visit2.setServices(new HashSet<>());
        visit2.setUser(customer);
        visit2.setVehicle(vehicle);
        visit2.setStartDate(LocalDate.now());
        visit2.setEndDate(LocalDate.now().plusDays(1));
        visit2.setServices(Set.of(
                new ServiceRecord(11, visit2.getId(), 2, "test2", 52)));

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit2);
        Mockito.when(servicesService.getById(Mockito.anyInt())).thenReturn(testService1);
        visitService.update(Helper.createEmployee(), visit1, Set.of(1, 2, 3));
        Mockito.verify(serviceRecordService, Mockito.times(1))
                .delete(Mockito.any(User.class), Mockito.anyInt());
        Mockito.verify(serviceRecordService, Mockito.times(1))
                .create(Mockito.any(User.class), Mockito.any(ServiceRecord.class), Mockito.any(Visit.class));
    }

    @Test
    void update_Should_Call_RepoMethod() {
        User customer = Helper.createCustomer();
        customer.setId(5);
        Vehicle vehicle = new Vehicle();
        vehicle.setUser(customer);
        Visit visit = new Visit();
        visit.setId(10);
        visit.setServices(new HashSet<>());
        visit.setUser(customer);
        visit.setVehicle(vehicle);
        visit.setStartDate(LocalDate.now());
        visit.setEndDate(LocalDate.now().plusDays(1));

        Mockito.when(userService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(customer);
        Mockito.when(vehicleService.getById(Mockito.any(User.class), Mockito.anyInt())).thenReturn(vehicle);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);

        visitService.update(Helper.createEmployee(), visit, new HashSet<>());
        Mockito.verify(visitRepository, Mockito.times(1)).update(visit);
    }

    @Test
    void delete_Throw_When_UserIsNotEmployee() {
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> visitService.softDelete(Helper.createCustomer(), 5));
    }

    @Test
    void delete_Should_Retrieve_OriginalFromRepo() {
        Visit visit = new Visit();
        visit.setStatus(StatusCode.NOT_STARTED.getStatus());
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        visitService.softDelete(Helper.createEmployee(), 10);
        Mockito.verify(visitRepository, Mockito.times(1)).getById(10);
    }

    @Test
    void delete_Throws_When_AlreadyDeleted() {
        Visit visit = new Visit();
        visit.setDeleted(true);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        Assertions.assertThrows(DuplicateEntityException.class, () ->
                visitService.softDelete(Helper.createEmployee(), 10));
    }

    @Test
    void delete_Should_Decline_RequestedVisits() {
        Visit visit = new Visit();
        visit.setStatus(StatusCode.REQUESTED.getStatus());
        visit.setDeleted(false);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        visitService.softDelete(Helper.createEmployee(), 10);
        Assertions.assertEquals(StatusCode.DECLINED.getStatus(), visit.getStatus());
    }


    @Test
    void delete_Should_Update_VisitDeleteStatus() {
        Visit visit = new Visit();
        visit.setStatus(StatusCode.NOT_STARTED.getStatus());
        visit.setDeleted(false);
        Mockito.when(visitRepository.getById(Mockito.anyInt())).thenReturn(visit);
        visitService.softDelete(Helper.createEmployee(), 10);
        Assertions.assertTrue(visit.isDeleted());
        Mockito.verify(visitRepository, Mockito.times(1)).update(visit);
    }

    @Test
    void getStatus_Should_Call_RepoMethod() {
        visitService.getStatus();
        Mockito.verify(visitRepository, Mockito.times(1)).getStatus();
    }
}
