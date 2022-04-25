package com.smart.garage.models.dtos;

import com.smart.garage.models.Currencies;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChargeRequest {

    private String description;
    private int amount;
    private Currencies currency;
    private String stripeEmail;
    private String stripeToken;
}