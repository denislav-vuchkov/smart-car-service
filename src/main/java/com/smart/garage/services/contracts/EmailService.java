package com.smart.garage.services.contracts;

import com.smart.garage.models.User;

import java.io.File;

public interface EmailService {

    void send(String to, String email, String subject, File pdfReport);

    String buildResetPasswordEmail(String link);

    String buildWelcomeEmail(User user);

    String buildPasswordEmail(User user);

    String buildReportEmail(User user);

}
