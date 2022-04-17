package com.smart.garage.services;

import com.smart.garage.Helper;
import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.models.User;
import com.smart.garage.repositories.contracts.UsersRepository;
import com.smart.garage.services.contracts.EmailService;
import com.smart.garage.services.contracts.ResetPasswordTokensService;
import com.smart.garage.utility.PasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UsersRepository repository;

    @Mock
    ResetPasswordTokensService resetPasswordTokensService;

    @Mock
    EmailService emailService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void getAll_Should_Throw_When_RequesterIsCustomer() {
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAll(requester));
    }

    @Test
    void getAll_Should_CallRepository() {
        User requester = Helper.createEmployee();
        userService.getAll(requester);
        Mockito.verify(repository, Mockito.times(1)).getAll();
    }

    @Test
    void getAllFiltered_Should_Throw_When_InvalidRoleID() {
        User requester = Helper.createCustomer();
        Assertions.assertThrows(InvalidParameter.class,
                () -> userService.getAllFiltered(-2, requester, Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    void getAllFiltered_Should_Throw_When_RequesterIsCustomer() {
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.getAllFiltered(3, requester, Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    void getAllFiltered_Should_Execute_When_ValidInput() {
        User requester = Helper.createEmployee();
        List<User> expected = Helper.getListOfCustomers();

        Mockito.when(userService.getAllFiltered(3, requester, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()))
                        .thenReturn(expected);

        List<User> actual = userService.getAllFiltered(3, requester, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty());

        Assertions.assertDoesNotThrow(() -> userService.getAllFiltered(3, requester, Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()));
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getByID_Should_CallRepo_When_ValidInput() {
        User requester = Helper.createEmployee();
        Mockito.when(repository.getById(requester.getId())).thenReturn(requester);
        userService.getById(requester, requester.getId());
        Mockito.verify(repository, Mockito.times(1)).getById(requester.getId());
    }

    @Test
    void getByID_Should_Throw_When_InvalidUserID() {
        User requester = Helper.createEmployee();
        Assertions.assertThrows(InvalidParameter.class, () -> userService.getById(requester, -2));
    }

    @Test
    void getByID_Should_Throw_When_CustomersAttemptsToAccessAnotherProfile() {
        User requester = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.getById(requester, 2));
    }

    @Test
    void create_Should_Throw_When_RequesterIsCustomer() {
        User requester = Helper.createCustomer();
        User toBeCreated = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.create(requester, toBeCreated));
    }

    @Test
    void create_Should_Throw_When_DuplicateUsername() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.doThrow(DuplicateEntityException.class)
                .when(repository).verifyFieldIsUnique("username", toBeCreated.getUsername());

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.create(requester, toBeCreated));
    }

    @Test
    void create_Should_Throw_When_DuplicateEmail() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.doNothing().when(repository).verifyFieldIsUnique("username", toBeCreated.getUsername());
        Mockito.doThrow(DuplicateEntityException.class)
                .when(repository).verifyFieldIsUnique("email", toBeCreated.getEmail());

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.create(requester, toBeCreated));
    }

    @Test
    void create_Should_Throw_When_DuplicatePhoneNumber() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.doNothing().when(repository).verifyFieldIsUnique("username", toBeCreated.getUsername());
        Mockito.doNothing().when(repository).verifyFieldIsUnique("email", toBeCreated.getEmail());
        Mockito.doThrow(DuplicateEntityException.class)
                .when(repository).verifyFieldIsUnique("phoneNumber", toBeCreated.getPhoneNumber());

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.create(requester, toBeCreated));
    }

    @Test
    void create_Should_Execute_When_ValidInput() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.when(passwordEncoder.getBCryptPasswordEncoder()).thenReturn(new BCryptPasswordEncoder());

        Assertions.assertDoesNotThrow(() -> userService.create(requester, toBeCreated));
        Mockito.verify(repository, Mockito.times(1)).create(toBeCreated);
    }


    @Test
    void update_Should_Throw_When_RequesterIsCustomer() {
        User requester = Helper.createCustomer();
        User toBeCreated = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class, () -> userService.update(requester, toBeCreated));
    }

    @Test
    void update_Should_Throw_When_DuplicateUsername() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.when(repository.getBySpecificField("username", toBeCreated.getUsername()))
                        .thenReturn(requester);

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.update(requester, toBeCreated));
    }

    @Test
    void update_Should_Throw_When_DuplicateEmail() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.when(repository.getBySpecificField("username", toBeCreated.getUsername()))
                .thenReturn(toBeCreated);
        Mockito.when(repository.getBySpecificField("email", toBeCreated.getEmail()))
                .thenReturn(requester);

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.update(requester, toBeCreated));
    }

    @Test
    void update_Should_Throw_When_DuplicatePhoneNumber() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.when(repository.getBySpecificField("username", toBeCreated.getUsername()))
                .thenReturn(toBeCreated);
        Mockito.when(repository.getBySpecificField("email", toBeCreated.getEmail()))
                .thenReturn(toBeCreated);
        Mockito.when(repository.getBySpecificField("phone_number", toBeCreated.getPhoneNumber()))
                .thenReturn(requester);

        Assertions.assertThrows(DuplicateEntityException.class, () -> userService.update(requester, toBeCreated));
    }

    @Test
    void update_Should_Execute_When_ValidInput() {
        User requester = Helper.createEmployee();
        User toBeCreated = Helper.createCustomer();

        Mockito.when(repository.getBySpecificField("username", toBeCreated.getUsername()))
                .thenReturn(toBeCreated);
        Mockito.when(repository.getBySpecificField("email", toBeCreated.getEmail()))
                .thenReturn(toBeCreated);
        Mockito.when(repository.getBySpecificField("phone_number", toBeCreated.getPhoneNumber()))
                .thenReturn(toBeCreated);

        Assertions.assertDoesNotThrow(() -> userService.update(requester, toBeCreated));
        Mockito.verify(repository, Mockito.times(1)).update(toBeCreated);
    }

    @Test
    void updateContactDetails_Should_Throw_When_RequesterNotOwner() {
        User requester = Helper.createCustomer();
        requester.setId(55);
        User toBeUpdated = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.updateContactDetails(requester, toBeUpdated));
    }

    @Test
    void updateContactDetails_Should_Throw_When_RequesterIsEmployee() {
        User requester = Helper.createEmployee();
        requester.setId(1);
        User toBeUpdated = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.updateContactDetails(requester, toBeUpdated));
    }

    @Test
    void updateContactDetails_Should_Throw_When_DuplicateEmail() {
        User requester = Helper.createCustomer();
        User toBeUpdated = Helper.createCustomer();
        User randomCustomer = Helper.createEmployee();

        Mockito.when(repository.getBySpecificField("email", toBeUpdated.getEmail()))
                .thenReturn(randomCustomer);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.updateContactDetails(requester, toBeUpdated));
    }

    @Test
    void updateContactDetails_Should_Throw_When_DuplicatePhoneNumber() {
        User requester = Helper.createCustomer();
        User toBeUpdated = Helper.createCustomer();
        User randomCustomer = Helper.createEmployee();

        Mockito.when(repository.getBySpecificField("email", toBeUpdated.getEmail()))
                .thenReturn(toBeUpdated);
        Mockito.when(repository.getBySpecificField("phone_number", toBeUpdated.getPhoneNumber()))
                .thenReturn(randomCustomer);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.updateContactDetails(requester, toBeUpdated));
    }

    @Test
    void updateContactDetails_Should_Execute_When_ValidInput() {
        User requester = Helper.createCustomer();
        User toBeUpdated = Helper.createCustomer();

        Mockito.when(repository.getBySpecificField("email", toBeUpdated.getEmail()))
                .thenReturn(toBeUpdated);
        Mockito.when(repository.getBySpecificField("phone_number", toBeUpdated.getPhoneNumber()))
                .thenReturn(toBeUpdated);

        Assertions.assertDoesNotThrow(() -> userService.updateContactDetails(requester, toBeUpdated));
        Mockito.verify(repository, Mockito.times(1)).update(toBeUpdated);
    }

    @Test
    void verifyFieldIsUnique_Should_CallRepository_When_ValidInput() {
        userService.verifyFieldIsUnique("field", "value");
        Mockito.verify(repository, Mockito.times(1)).verifyFieldIsUnique("field", "value");
    }

    @Test
    void updatePassword_Should_Throw_When_InvalidID() {
        User requester = Helper.createEmployee();
        Assertions.assertThrows(InvalidParameter.class,
                () -> userService.updatePassword(requester, -20, requester.getPassword()));
    }

    @Test
    void updatePassword_Should_Throw_When_UserWithIdDoesntExist() {
        User requester = Helper.createEmployee();

        Mockito.when(repository.getById(requester.getId())).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.updatePassword(requester, requester.getId(), requester.getPassword()));
    }

    @Test
    void updatePassword_Should_Throw_When_RequesterIsNotOwner() {
        User requester = Helper.createEmployee();
        User user = Helper.createCustomer();

        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn(user);

        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.updatePassword(requester, 55, Mockito.anyString()));
    }

    @Test
    void updatePassword_Should_CallRepository_When_ValidInput() {
        User requester = Helper.createEmployee();

        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn(requester);
        Mockito.when(passwordEncoder.getBCryptPasswordEncoder()).thenReturn(new BCryptPasswordEncoder());

        userService.updatePassword(requester, requester.getId(), requester.getPassword());

        Mockito.verify(repository, Mockito.times(1)).update(requester);
    }


    @Test
    void softDelete_Should_Throw_When_InvalidUserID() {
        User requester = Helper.createEmployee();
        Assertions.assertThrows(InvalidParameter.class,
                () -> userService.softDelete(requester, -20));
    }

    @Test
    void softDelete_Should_Throw_When_RequesterIsCustomer() {
        User requested = Helper.createCustomer();
        Assertions.assertThrows(UnauthorizedOperationException.class,
                () -> userService.softDelete(requested, 2));
    }

    @Test
    void softDelete_Should_Throw_When_NonExistingUserID() {
        User requester = Helper.createEmployee();
        Mockito.when(repository.getById(Mockito.anyInt())).thenThrow(EntityNotFoundException.class);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.softDelete(requester, 5));
    }

    @Test
    void softDelete_Should_Throw_When_UserIsAlreadyDeleted() {
        User requester = Helper.createEmployee();
        User userToDelete = Helper.createCustomer();
        userToDelete.setDeleted(true);

        Mockito.when(repository.getById(Mockito.anyInt())).thenReturn(userToDelete);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.softDelete(requester, 5));
    }

    @Test
    void softDelete_Should_CallRepository_When_InputIsValid() {
        User requester = Helper.createEmployee();
        User userToDelete = Helper.createCustomer();

        Mockito.when(repository.getById(userToDelete.getId())).thenReturn(userToDelete);

        userService.softDelete(requester, userToDelete.getId());

        Mockito.verify(repository, Mockito.times(1)).update(userToDelete);
    }

    @Test
    void getByField_Should_CallRepo() {
        userService.getByField(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(repository, Mockito.times(1))
                .getByField(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    void generateResetPassword_Should_Throw_When_UserIsDeleted() {
        User user = Helper.createCustomer();
        user.setDeleted(true);

        Assertions.assertThrows(DuplicateEntityException.class,
                () -> userService.generateResetPasswordEmail(user));
    }

    @Test
    void generateResetPassword_Should_Execute_When_UserIsActive() {
        User user = Helper.createCustomer();

        Mockito.doNothing().when(resetPasswordTokensService).create(Mockito.any(ResetPasswordTokens.class));

        Assertions.assertDoesNotThrow(() -> userService.generateResetPasswordEmail(user));
    }

    @Test
    void confirmToken_Should_Throw_When_TokenDoesntExist() {
        ResetPasswordTokens token = new ResetPasswordTokens();

        Mockito.when(resetPasswordTokensService.findByToken(token.getToken()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.confirmToken(token.getToken()));
    }

    @Test
    void confirmToken_Should_Throw_When_TokenHasBeenAccessed() {
        ResetPasswordTokens token = new ResetPasswordTokens();
        token.setAccessed(true);

        Mockito.when(resetPasswordTokensService.findByToken(token.getToken())).thenReturn(token);

        Assertions.assertThrows(IllegalStateException.class,
                () -> userService.confirmToken(token.getToken()));
    }

    @Test
    void confirmToken_Should_Throw_When_TokenHasExpired() {
        ResetPasswordTokens token = new ResetPasswordTokens();
        token.setExpiredAt(LocalDateTime.now().minusHours(2));

        Mockito.when(resetPasswordTokensService.findByToken(token.getToken())).thenReturn(token);

        Assertions.assertThrows(IllegalStateException.class,
                () -> userService.confirmToken(token.getToken()));
    }

    @Test
    void confirmToken_Should_Execute_When_TokenNotExpired() {
        ResetPasswordTokens token = new ResetPasswordTokens();
        token.setExpiredAt(LocalDateTime.now().plusHours(1));

        Mockito.when(resetPasswordTokensService.findByToken(token.getToken())).thenReturn(token);

        Assertions.assertDoesNotThrow(() -> userService.confirmToken(token.getToken()));
    }




}
