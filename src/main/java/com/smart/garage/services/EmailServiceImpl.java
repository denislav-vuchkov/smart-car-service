package com.smart.garage.services;

import com.smart.garage.models.User;
import com.smart.garage.models.Vehicle;
import com.smart.garage.services.contracts.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;

@Service
@Async
public class EmailServiceImpl implements EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final static String username = "smartgaragetelerik@gmail.com";
    private final static String password = "cvzrodfhbgajtebe";
    public static final String WELCOME_TO_OUR_SMART_GARAGE = "Welcome to our Smart Garage!";
    public static final String PASSWORD_RESET = "Password reset";
    public static final String YOUR_PASSWORD = "Your password";
    public static final String VISIT_REPORT = "Visit report";
    public static final String VISIT_CONFIRMATION = "Visit confirmation";

    @Override
    public void send(String to, String email, String subject, ByteArrayOutputStream stream, String filName) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress("smartgaragetelerik@gmail.com"));
            mimeMessage.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            mimeMessage.setSubject(subject);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(email, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (stream != null) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource dataSource = new ByteArrayDataSource(stream.toByteArray(),
                        "application/octet-stream");
                attachmentPart.setDataHandler(new DataHandler(dataSource));
                attachmentPart.setFileName(filName);
                multipart.addBodyPart(attachmentPart);
            }

            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);

        } catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    public String buildResetPasswordEmail(String link) {
        return getEmailStart(PASSWORD_RESET) +
                getMainPartForResetPassword(link) +
                getEnd();
    }

    private String getMainPartForResetPassword(String link) {
        return "<tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> To reset your password please click on the below link and enter your new password: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Reset Password</a> </p></blockquote>\n Link will expire in 60 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n";
    }

    @Override
    public String buildWelcomeEmail(User user) {
        return getEmailStart(WELCOME_TO_OUR_SMART_GARAGE) +
                getMainPartForWelcome(user) +
                getEnd();
    }

    private String getMainPartForWelcome(User user) {
        return "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Our team has created an account for you in the best Smart Garage! Here are your details: </p> <span style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Username: <span> <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight: bold\"> " + user.getUsername() + " </p> <span style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Phone number: </span> <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight: bold\"> " + user.getPhoneNumber() + " </p> <span style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Email: <span> <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight: bold\"> " + user.getEmail() + " </p> <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">You will shortly receive your password in a separate email." +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n";
    }

    @Override
    public String buildPasswordEmail(User user) {
        return getEmailStart(YOUR_PASSWORD) +
                getMainPartForPassword(user) +
                getEnd();
    }

    private String getMainPartForPassword(User user) {
        return "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi (it's us again),</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Here is your secure automatically generated password: </p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c; font-weight: bold\"> " + user.getPassword() + " </p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Make sure your do not share it with anyone! If you forget it there is a way to reset it ;) " +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n";
    }

    @Override
    public String buildReportEmail(User user) {
        return getEmailStart(VISIT_REPORT) +
                getMainPartForVisit() +
                getEnd();
    }

    private String getMainPartForVisit() {
        return "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Our team has generated a report for the visits you selected. </p> <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight: bold\"> Please find your report attached </p>  <p style=\"Margin:0 0 20px 0;font-size:19px;margin-top:25px;line-height:25px;color:#0b0c0c\">Don't hesitate to contact us if any questions. </p> <p style=\"font-size:19px;margin-bottom:5px;color:#0b0c0c\">Kind regards, </p> <p style=\"font-size:19px;color:#0b0c0c\">Tihomir and Denislav</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n";
    }

    @Override
    public String buildVisitConfirmationEmail(Vehicle vehicle, LocalDate date) {
        return getEmailStart(VISIT_CONFIRMATION) +
                getMainPartForVisitConfirmation(vehicle, date) +
                getEnd();
    }

    private String getMainPartForVisitConfirmation(Vehicle vehicle, LocalDate date) {
        return "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for using our online booking form! <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;font-weight: bold\"> Your visit for " + vehicle.makeName() + " " + vehicle.modelName() + " " + vehicle.getLicense() + " on " + date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + " has been confirmed. Our team is looking forward to see you! </p>  <p style=\"Margin:0 0 20px 0;font-size:19px;margin-top:25px;line-height:25px;color:#0b0c0c\">Don't hesitate to contact us if any questions. </p> <p style=\"font-size:19px;margin-bottom:5px;color:#0b0c0c\">Kind regards, </p> <p style=\"font-size:19px;color:#0b0c0c\">Tihomir and Denislav</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n";
    }

    private String getEmailStart(String message) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\"> " + message + "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n";
    }

    private String getEnd() {
        return "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

}
