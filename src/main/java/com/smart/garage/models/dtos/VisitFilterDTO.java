package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitFilterDTO {

    private Set<Integer> ownerFilter;
    private Set<Integer> vehicleFilter;
    private String statusFilter;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateFrom;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateTo;
    private String sorting;
    private String order;

}
