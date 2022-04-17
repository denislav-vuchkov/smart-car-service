package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserFilterDTO {

    private String username;
    private String email;
    private String phoneNumber;
    private String licenseOrVIN;
    private String make;
    private String model;
    private String sortBy;
    private String sortOrder;

}
