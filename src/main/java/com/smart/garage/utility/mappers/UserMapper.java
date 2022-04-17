package com.smart.garage.utility.mappers;

import com.smart.garage.exceptions.EntityNotFoundException;
import com.smart.garage.models.Role;
import com.smart.garage.models.User;
import com.smart.garage.models.dtos.ContactDetailsDTO;
import com.smart.garage.models.UserRoles;
import com.smart.garage.models.dtos.NewCustomerDTO;
import com.smart.garage.models.dtos.UserDTOIn;
import com.smart.garage.models.dtos.UserDTOOut;
import com.smart.garage.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class UserMapper {

    private final int[] validRoles = new int[]{1, 2, 3};

    private final UserService service;

    @Autowired
    public UserMapper(UserService service) {
        this.service = service;
    }

    public UserDTOOut toDTOOut(User user) {
        UserDTOOut userDTOOut = new UserDTOOut();

        userDTOOut.setId(user.getId());
        userDTOOut.setUsername(user.getUsername());
        userDTOOut.setEmail(user.getEmail());
        userDTOOut.setPhoneNumber(user.getPhoneNumber());
        userDTOOut.setRole(user.getRole());
        userDTOOut.setDeleted(user.isDeleted());
        userDTOOut.setRegisteredAt(user.getRegisteredAt());
        userDTOOut.setVehicleSet(user.getVehicleSet());

        return userDTOOut;
    }

    public UserDTOIn toDTOIn(User user) {
        UserDTOIn userDTOIn = new UserDTOIn();

        userDTOIn.setUsername(user.getUsername());
        userDTOIn.setEmail(user.getEmail());
        userDTOIn.setPhoneNumber(user.getPhoneNumber());
        userDTOIn.setRoleId(user.getRole().getId());

        return userDTOIn;
    }

    public ContactDetailsDTO toContactsDTO(User user) {
        ContactDetailsDTO contactDetailsDTO = new ContactDetailsDTO();

        contactDetailsDTO.setEmail(user.getEmail());
        contactDetailsDTO.setPhoneNumber(user.getPhoneNumber());

        return contactDetailsDTO;
    }

    public User toObject(UserDTOIn userDTOIn) {
        User user = new User();

        user.setUsername(userDTOIn.getUsername());
        user.setEmail(userDTOIn.getEmail());
        user.setPhoneNumber(userDTOIn.getPhoneNumber());

        Role role = checkIfValidRole(userDTOIn.getRoleId());
        user.setRole(role);
        user.setRegisteredAt(LocalDateTime.now());

        return user;
    }

    public User toObject(User requester, int id, UserDTOIn userDTOIn) {
        User user = service.getById(requester, id);

        user.setUsername(userDTOIn.getUsername());
        user.setEmail(userDTOIn.getEmail());
        user.setPhoneNumber(userDTOIn.getPhoneNumber());

        return user;
    }

    public User toObject(NewCustomerDTO newCustomerDTO) {
        User user = new User();
        user.setUsername(newCustomerDTO.getUsername());
        user.setEmail(newCustomerDTO.getEmail());
        user.setPhoneNumber(newCustomerDTO.getPhoneNumber());
        user.setRole(checkIfValidRole(UserRoles.CUSTOMER.getValue()));
        user.setRegisteredAt(LocalDateTime.now());
        return user;
    }

    public User toObject(User requester, int id, ContactDetailsDTO contactDetailsDTO) {
        User user = service.getById(requester, id);

        user.setEmail(contactDetailsDTO.getEmail());
        user.setPhoneNumber(contactDetailsDTO.getPhoneNumber());

        return user;
    }

    private Role checkIfValidRole(int roleId) {
        if (Arrays.stream(validRoles).noneMatch(e -> e == roleId)) {
            throw new EntityNotFoundException("Role", "id", Integer.toString(roleId));
        }

        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}
