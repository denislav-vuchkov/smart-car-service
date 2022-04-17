package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleFilterDTO {

    private String identifierFilter;
    private Set<Integer> ownerFilter;
    private Set<String> brandFilter;
    private Set<Integer> yearFilter;
    private String sorting;
    private String order;

}
