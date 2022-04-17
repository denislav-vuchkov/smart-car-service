package com.smart.garage.services;

import com.smart.garage.Helper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplTests {

    @InjectMocks
    EmailServiceImpl service;

    @Test
    void send_Should_Execute_When_ValidInput(){
        Assertions.assertDoesNotThrow(() -> service.send("dimitrovtihomir7@gmail.com",
                "Testing the email service", "Test - ignore", new File(".gitignore")));
    }

    @Test
    void buildResetPasswordEmail_Should_Execute() {
        Assertions.assertDoesNotThrow(() -> service.buildResetPasswordEmail("someLink"));
    }

    @Test
    void buildPasswordEmail_Should_Execute() {
        Assertions.assertDoesNotThrow(() -> service.buildPasswordEmail(Helper.createCustomer()));
    }

    @Test
    void buildReportEmail_Should_Execute() {
        Assertions.assertDoesNotThrow(() -> service.buildReportEmail(Helper.createCustomer()));
    }

    @Test
    void buildWelcomeEmail_Should_Execute() {
        Assertions.assertDoesNotThrow(() -> service.buildWelcomeEmail(Helper.createCustomer()));
    }

}
