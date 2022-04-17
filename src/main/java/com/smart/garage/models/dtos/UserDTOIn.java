package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTOIn {

    public static final String USERNAME_ERROR = "Username must be between 2 and 20 symbols.";
    public static final String EMAIL_ERROR = "Please enter a valid email address.";
    public static final String VALID_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    public static final String PHONE_ERROR = "Please enter a valid phone number ";
    public static final String VALIDATE_PHONE = "^08[0-9]{8}$";

    @NotNull
    @Size(min = 2, max = 20, message = USERNAME_ERROR)
    private String username;

    @NotNull
    @Email(message = EMAIL_ERROR, regexp = VALID_EMAIL)
    private String email;

    @NotNull
    @Pattern(message = PHONE_ERROR, regexp = VALIDATE_PHONE)
    private String phoneNumber;

    @NotNull(message = "Please select a role.")
    @Positive(message = "Role selected must be a positive number.")
    private Integer roleId;

}
