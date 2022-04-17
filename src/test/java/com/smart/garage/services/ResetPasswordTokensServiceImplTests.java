package com.smart.garage.services;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.repositories.contracts.ResetPasswordTokensRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordTokensServiceImplTests {

    @Mock
    ResetPasswordTokensRepository repository;

    @InjectMocks
    ResetPasswordTokensServiceImpl service;

    @Test
    void findByToken_Should_Throw_When_InvalidToken() {
        Mockito.when(repository.getByField("token", "someToken")).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> service.findByToken("someToken"));
    }

    @Test
    void create_Should_CallRepository() {
        ResetPasswordTokens token = new ResetPasswordTokens();
        service.create(token);
        Mockito.verify(repository, Mockito.times(1)).create(token);
    }

    @Test
    void update_Should_CallRepository() {
        ResetPasswordTokens token = new ResetPasswordTokens();
        service.update(token);
        Mockito.verify(repository, Mockito.times(1)).update(token);
    }


}
