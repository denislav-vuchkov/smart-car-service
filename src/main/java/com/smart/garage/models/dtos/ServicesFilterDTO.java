package com.smart.garage.models.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicesFilterDTO {

    private String name;
    private int priceMinimum;
    private int priceMaximum;
    private String sortBy;
    private String sortOrder;


}
