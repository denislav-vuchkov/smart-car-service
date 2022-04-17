package com.smart.garage;

import com.smart.garage.models.Role;
import com.smart.garage.models.Servicez;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;

import java.time.LocalDateTime;
import java.util.List;

public class Helper {

    public static User createCustomer() {
        User user = new User();

        user.setId(1);
        user.setUsername("dummy_customer");
        user.setEmail("bestemail@gmail.com");
        user.setPhoneNumber("0876051461");
        user.setPassword("password12345678");

        Role role = new Role(3, UserRoles.CUSTOMER);
        user.setRole(role);
        user.setRegisteredAt(LocalDateTime.now());

        return user;
    }

    public static User createEmployee() {
        User user = new User();

        user.setId(2);
        user.setUsername("dummy_employee");
        user.setEmail("evenbetteremail@gmail.com");
        user.setPhoneNumber("0876051466");
        user.setPassword("password12345678");

        Role role = new Role(2, UserRoles.EMPLOYEE);
        user.setRole(role);
        user.setRegisteredAt(LocalDateTime.now());

        return user;
    }

    public static List<User> getListOfCustomers() {
        User customerOne = createCustomer();
        User customerTwo = createCustomer();
        customerTwo.setUsername("Tihomir");
        User customerThree = createCustomer();
        customerThree.setEmail("crypto@gmail.com");
        return List.of(customerOne, customerTwo, customerThree);
    }

    public static List<User> getListOfEmployees() {
        User employeeOne = createEmployee();
        User employeeTwo = createEmployee();
        employeeTwo.setUsername("Denislav");
        User employeeThree =  createEmployee();
        employeeThree.setEmail("crypto@gmail.com");
        return List.of(employeeOne, employeeTwo, employeeThree);
    }

    public static Servicez createExpensiveService() {
        Servicez service = new Servicez();
        service.setId(1);
        service.setName("Best service ever");
        service.setPriceBGN(999);
        return service;
    }

    public static Servicez createEngineService() {
        Servicez service = new Servicez();
        service.setId(2);
        service.setName("Engine service");
        service.setPriceBGN(222);
        return service;
    }

    public static Servicez createFuelPumpService() {
        Servicez service = new Servicez();
        service.setId(3);
        service.setName("Fuel pump service");
        service.setPriceBGN(444);
        return service;
    }

    public static List<Servicez> getListOfServices() {
        Servicez servicezOne = createExpensiveService();
        Servicez servicezTwo = createEngineService();
        Servicez servicezThree = createFuelPumpService();

        return List.of(servicezOne, servicezTwo, servicezThree);
    }

}
