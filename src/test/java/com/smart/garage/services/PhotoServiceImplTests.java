package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.Photo;
import com.smart.garage.models.User;
import com.smart.garage.models.Visit;
import com.smart.garage.repositories.contracts.PhotoRepository;
import com.smart.garage.services.contracts.VisitService;
import com.smart.garage.utility.CloudinaryHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PhotoServiceImplTests {

    @Mock
    PhotoRepository photoRepository;

    @Mock
    CloudinaryHelper cloudinaryHelper;

    @Mock
    VisitService visitService;

    @InjectMocks
    PhotoServiceImpl photoService;

    @Test
    void delete_Should_Throw_When_InvalidVisitID() {
        User requester = Helper.createEmployee();

        Mockito.when(visitService.getById(requester, 5)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> photoService.delete(requester, 5, "random"));
    }

    @Test
    void delete_Should_Throw_When_InvalidToken() {
        User requester = Helper.createEmployee();

        Mockito.when(visitService.getById(requester, 5)).thenReturn(new Visit());
        Mockito.when(photoRepository.getByField("token", "random")).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> photoService.delete(requester, 5, "random"));
    }

    @Test
    void delete_Should_Execute_When_ValidInput() {
        User requester = Helper.createEmployee();

        Mockito.when(visitService.getById(requester, 5)).thenReturn(new Visit());
        Mockito.when(photoRepository.getByField("token", "random")).thenReturn(new Photo());

        Assertions.assertDoesNotThrow(() -> photoService.delete(requester, 5, "random"));

    }

}
