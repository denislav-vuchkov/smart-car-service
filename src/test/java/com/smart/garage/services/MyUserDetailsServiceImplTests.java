package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.User;
import com.smart.garage.repositories.contracts.UsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceImplTests {

    @Mock
    UsersRepository repository;

    @InjectMocks
    MyUserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_Should_Throw_When_UsernameNotFound() {
        Mockito.when(repository.getByField("username", "random username"))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("random username"));
    }

    @Test
    void loadUserByUsername_Should_ReturnUser_When_ValidInput() {
        User user = Helper.createCustomer();

        Mockito.when(repository.getByField("username", user.getUsername())).thenReturn(user);
        Assertions.assertDoesNotThrow(() -> service.loadUserByUsername(user.getUsername()));
    }

}
