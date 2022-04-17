package com.smart.garage.services;

import com.smart.garage.repositories.contracts.RolesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RolesServiceImplTests {

    @Mock
    RolesRepository repository;

    @InjectMocks
    RolesServiceImpl service;

    @Test
    void getAll_Should_CallRepository() {
        service.getAll();
        Mockito.verify(repository, Mockito.times(1)).getAll();
    }

}
