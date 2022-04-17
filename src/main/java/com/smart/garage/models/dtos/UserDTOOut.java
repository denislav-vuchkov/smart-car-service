package com.smart.garage.models.dtos;

import com.smart.garage.models.Role;
import com.smart.garage.models.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTOOut {

    private int id;
    private String username;
    private String email;
    private String phoneNumber;
    private Role role;
    private boolean isDeleted;
    private LocalDateTime registeredAt;
    private Set<Vehicle> vehicleSet;

}
