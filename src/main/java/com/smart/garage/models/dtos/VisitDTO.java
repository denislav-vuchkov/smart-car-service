package com.smart.garage.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

import static com.smart.garage.utility.VisitDataExtractor.formatDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisitDTO {

    public static final String MANDATORY_ATTRIBUTE = "Visits must have ";
    public static final String INVALID_DATE = "Invalid date format.";

    @Positive(message = MANDATORY_ATTRIBUTE + "a vehicle.")
    private int vehicleID;

    @Positive(message = MANDATORY_ATTRIBUTE + "a status.")
    private int statusID;

    @Size(min = 10, max = 10, message = INVALID_DATE)
    private String startDate = formatDate(LocalDate.now());

    @Size(max = 10, message = INVALID_DATE)
    private String endDate;

    @NotEmpty(message = MANDATORY_ATTRIBUTE + "at least one service.")
    private Set<Integer> serviceIDs;
}
