package com.smart.garage.utility;

import com.smart.garage.exceptions.InvalidParameter;
import com.smart.garage.models.Visit;
import com.smart.garage.models.dtos.VisitDTO;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VisitDateExtractor {

    public static final String DATE_PICKER_FORMAT = "MM/dd/yyyy";
    public static final String INVALID_DATE_ERROR = "Invalid date format.";
    private static final String DATE_MISMATCH = "End date cannot be before start date.";

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PICKER_FORMAT));
        } catch (DateTimeException e) {
            throw new InvalidParameter(INVALID_DATE_ERROR);
        }
    }

    public static String formatDate(LocalDate date) {
        try {
            return date.format(DateTimeFormatter.ofPattern(DATE_PICKER_FORMAT));
        } catch (DateTimeException e) {
            throw new InvalidParameter(INVALID_DATE_ERROR);
        }
    }

    public static LocalDate parseEndDate(VisitDTO visitDTO) {
        return (visitDTO.getEndDate() == null || visitDTO.getEndDate().isBlank())
                ? null : parseDate(visitDTO.getEndDate());
    }

    public static String formatEndDate(Visit visit) {
        return visit.getEndDate() == null ? null : formatDate(visit.getEndDate());
    }

    public static void validateStartAndEndDatesChronological(Visit visit) {
        if (visit.getEndDate() != null) {
            if (visit.getEndDate().isBefore(visit.getStartDate())) throw new InvalidParameter(DATE_MISMATCH);
        }
    }
}
