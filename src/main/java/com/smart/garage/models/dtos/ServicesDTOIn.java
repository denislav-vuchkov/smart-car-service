package com.smart.garage.models.dtos;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesDTOIn {

    @NotNull
    @Size(min = 5, max = 50, message = "Service name must be between 5 and 50 symbols.")
    private String serviceName;

    @Positive(message = "Price must be a positive number")
    @NotNull
    private int price;

}
