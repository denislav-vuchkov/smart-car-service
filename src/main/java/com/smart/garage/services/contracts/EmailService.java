package com.smart.garage.services.contracts;

import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public interface EmailService {

    void send(String to, String email, String subject, ByteArrayOutputStream attachment, String fileName);

    String buildResetPasswordEmail(String link);

    String buildWelcomeEmail(User user);

    String buildPasswordEmail(User user);

    String buildReportEmail(User user);

    String buildVisitConfirmationEmail(Vehicle vehicle, LocalDate date);

}
