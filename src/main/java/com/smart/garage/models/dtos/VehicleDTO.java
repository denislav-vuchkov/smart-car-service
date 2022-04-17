package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {

    public static final String MANDATORY_ATTRIBUTE = "Vehicles must have ";

    public static final String VALID_VIN = "^(?=.*[0-9])(?=.*[A-z])[0-9A-z-]{17}$";
    public static final String ERROR_VIN = "Input for VIN is not valid.";

    public static final String VALID_LICENSE = "^[АВЕКМНОРСТУХABEKMHOPCTX]{1,2}[0-9]{4}[АВЕКМНОРСТУХABEKMHOPCTX]{2}$";
    public static final String ERROR_LICENSE = "Input for license is not valid.";
    public static final String YER_RANGE = "Year of manufacture must be between 1886 and 2002.";

    @NotNull(message = MANDATORY_ATTRIBUTE + "a license.")
    @Pattern(regexp = VALID_LICENSE, message = ERROR_LICENSE)
    private String license;

    @NotNull(message = MANDATORY_ATTRIBUTE + "a VIN.")
    @Pattern(regexp = VALID_VIN, message = ERROR_VIN)
    private String VIN;

    @Positive(message = MANDATORY_ATTRIBUTE + "an owner.")
    private int ownerID;

    @Min(value = 1886, message = YER_RANGE)
    @Max(value = 2022, message = YER_RANGE)
    private int year;

    private VehicleModelDTO vehicleModelDTO;

}
