package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

import static com.smart.garage.utility.VisitDateExtractor.formatDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCustomerDTO {

    @NotNull
    @Size(min = 2, max = 20, message = UserDTOIn.USERNAME_ERROR)
    private String username;

    @NotNull
    @Email(message = UserDTOIn.EMAIL_ERROR, regexp = UserDTOIn.VALID_EMAIL)
    private String email;

    @NotNull
    @Pattern(message = UserDTOIn.PHONE_ERROR, regexp = UserDTOIn.VALIDATE_PHONE)
    private String phoneNumber;


    @NotNull(message = VehicleDTO.MANDATORY_ATTRIBUTE + "a license.")
    @Pattern(regexp = VehicleDTO.VALID_LICENSE, message = VehicleDTO.ERROR_LICENSE)
    private String license;

    @NotNull(message = VehicleDTO.MANDATORY_ATTRIBUTE + "a VIN.")
    @Pattern(regexp = VehicleDTO.VALID_VIN, message = VehicleDTO.ERROR_VIN)
    private String VIN;

    @Min(value = 1886, message = VehicleDTO.YER_RANGE)
    @Max(value = 2022, message = VehicleDTO.YER_RANGE)
    private int year;

    private VehicleModelDTO vehicleModelDTO;

    @Positive(message = VisitDTO.MANDATORY_ATTRIBUTE + "a status.")
    private int statusID;

    @Size(min = 10, max = 10, message = VisitDTO.INVALID_DATE)
    private String startDate = formatDate(LocalDate.now());

    @Size(max = 10, message = VisitDTO.INVALID_DATE)
    private String endDate;

    @NotEmpty(message = VisitDTO.MANDATORY_ATTRIBUTE + "at least one service.")
    private Set<Integer> serviceIDs;

}
