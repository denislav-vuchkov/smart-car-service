package com.smart.garage.services;

import com.smart.garage.exceptions.DuplicateEntityException;
import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.exceptions.UnauthorizedOperationException;
import com.smart.garage.models.ResetPasswordTokens;
import com.smart.garage.models.User;
import com.smart.garage.models.UserRoles;
import com.smart.garage.repositories.contracts.UsersRepository;
import com.smart.garage.services.contracts.EmailService;
import com.smart.garage.services.contracts.ResetPasswordTokensService;
import com.smart.garage.services.contracts.UserService;
import com.smart.garage.utility.PasswordEncoder;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.smart.garage.utility.AuthenticationHelper.RESTRICTED_FOR_EMPLOYEES;
import static com.smart.garage.utility.AuthenticationHelper.RESTRICTED_FOR_OWNER;
import static org.passay.CharacterOccurrencesRule.ERROR_CODE;

@Service
public class UserServiceImpl implements UserService {

    public static final String USER_DELETED_MESSAGE = "User has already been deleted.";
    private final UsersRepository usersRepository;
    private final EmailService emailService;
    private final ResetPasswordTokensService resetPasswordTokensService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository,
                           EmailService emailService,
                           ResetPasswordTokensService resetPasswordTokensService, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.emailService = emailService;
        this.resetPasswordTokensService = resetPasswordTokensService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAll(User requester) {
        throwIfRequesterIsCustomer(requester);
        return usersRepository.getAll();
    }

    @Override
    public List<User> getAllFiltered(int roleId, User requester, Optional<String> username, Optional<String> email,
                                     Optional<String> phoneNumber, Optional<String> licenseOrVIN, Optional<String> make,
                                     Optional<String> model, Optional<String> sortBy, Optional<String> sortOrder) {
        validateRoleId(roleId);
        if (roleId == UserRoles.CUSTOMER.getValue()) throwIfRequesterIsCustomer(requester);
        return usersRepository.getAllFiltered(roleId, username, email, phoneNumber, licenseOrVIN, make, model, sortBy, sortOrder);
    }

    @Override
    public User getById(User requester, int id) {
        validateUserID(id);
        if (requester.getId() != id) throwIfRequesterIsCustomer(requester);
        return usersRepository.getById(id);
    }

    @Override
    public User create(User requester, User user) {
        throwIfRequesterIsCustomer(requester);

        usersRepository.verifyFieldIsUnique("username", user.getUsername());
        usersRepository.verifyFieldIsUnique("email", user.getEmail());
        usersRepository.verifyFieldIsUnique("phoneNumber", user.getPhoneNumber());

        String password = generatePassayPassword();
        user.setPassword(password);

        emailService.send(user.getEmail(), emailService.buildWelcomeEmail(user), "Smart Garage - Welcome!", null, null);
        emailService.send(user.getEmail(), emailService.buildPasswordEmail(user), "Smart Garage - Your Password!", null, null);

        BCryptPasswordEncoder encoder = passwordEncoder.getBCryptPasswordEncoder();
        password = encoder.encode(password);
        user.setPassword(password);

        usersRepository.create(user);

        return user;
    }

    @Override
    public User update(User requester, User user) {
        throwIfRequesterIsCustomer(requester);

        verifyFieldIsUniqueIfChanged("username", user.getUsername(), user.getId());
        verifyFieldIsUniqueIfChanged("email", user.getEmail(), user.getId());
        verifyFieldIsUniqueIfChanged("phone_number", user.getPhoneNumber(), user.getId());

        usersRepository.update(user);

        return user;
    }

    @Override
    public User updateContactDetails(User requester, User user) {
        if (requester.getId() != user.getId() || requester.getRole().getName() != UserRoles.CUSTOMER) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER);
        }

        verifyFieldIsUniqueIfChanged("email", user.getEmail(), user.getId());
        verifyFieldIsUniqueIfChanged("phone_number", user.getPhoneNumber(), user.getId());

        usersRepository.update(user);

        return user;
    }

    @Override
    public void verifyFieldIsUnique(String field, String value) {
        usersRepository.verifyFieldIsUnique(field, value);
    }

    @Override
    public void updatePassword(User requester, int id, String password) {
        validateUserID(id);
        User user = usersRepository.getById(id);

        if (requester.getId() != id) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_OWNER);
        }

        BCryptPasswordEncoder encoder = passwordEncoder.getBCryptPasswordEncoder();
        password = encoder.encode(password);
        user.setPassword(password);

        usersRepository.update(user);
    }

    @Override
    public void softDelete(User requester, int id) {
        validateUserID(id);
        throwIfRequesterIsCustomer(requester);

        User user = usersRepository.getById(id);

        if (user.isDeleted()) {
            throw new DuplicateEntityException(USER_DELETED_MESSAGE);
        }

        user.setDeleted(true);
        usersRepository.update(user);
    }

    @Override
    public User getByField(String field, String value) {
        return usersRepository.getByField(field, value);
    }

    @Override
    public void generateResetPasswordEmail(User user) {
        if (user.isDeleted()) throw new DuplicateEntityException(USER_DELETED_MESSAGE);

        String token = UUID.randomUUID().toString();
        ResetPasswordTokens confirmationToken = new ResetPasswordTokens(token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(60),
                user);

        resetPasswordTokensService.create(confirmationToken);

        sendEmailWithResetPasswordToken(user, token);
    }

    @Override
    public void confirmToken(String token) {
        ResetPasswordTokens confirmationToken = resetPasswordTokensService.findByToken(token);

        if (confirmationToken.isAccessed()) {
            throw new IllegalStateException("Your reset password link has already been accessed.");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiredAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Your reset password link has expired.");
        }

        confirmationToken.setAccessed(true);

        resetPasswordTokensService.update(confirmationToken);
    }

    private void verifyFieldIsUniqueIfChanged(String field, String value, int id) {
        User user = usersRepository.getBySpecificField(field, value);

        if (user != null && user.getId() != id) {
            throw new DuplicateEntityException("User", field, value);
        }
    }

    private void validateRoleId(int roleId) {
        UserRoles[] roles = UserRoles.values();
        if (Arrays.stream(roles).map(UserRoles::getValue).noneMatch(id -> id == roleId)) {
            throw new InvalidParameter("Please provide a valid user role id.");
        }
    }

    private void validateUserID(int id) {
        if (id <= 0) throw new InvalidParameter("Please provide a valid user id.");
    }

    private String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();

        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(1);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(1);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(1);

        return gen.generatePassword(25, splCharRule, lowerCaseRule, upperCaseRule, digitRule);

    }

    private void throwIfRequesterIsCustomer(User user) {
        if (user.getRole().getName() != UserRoles.EMPLOYEE) {
            throw new UnauthorizedOperationException(RESTRICTED_FOR_EMPLOYEES);
        }
    }

    private void sendEmailWithResetPasswordToken(User user, String token) {
        String link = "http://smartgarage.shop/reset-password?token=" + token;
        emailService.send(user.getEmail(), emailService.buildResetPasswordEmail(link), "Smart Garage - Reset your password", null, null);
    }

}
