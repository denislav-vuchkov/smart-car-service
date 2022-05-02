package com.smart.garage.utility;

import com.smart.garage.models.Visit;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SMSHelper {

    public static final String ERROR_MESSAGE = "There was an issue with sending your confirmation SMS but your payment has been successfully processed.";
    public static final String THANK_YOU_SMS = "Thank you for your payment for the services on your %s %s with license plate %s. \n Your payment ID is %s if you ever need it. \n Smart Garage Team";

    private final String twilioSID = System.getenv("TWILIO_SID");
    private final String twilioToken = System.getenv("TWILIO_AUTH_TOKEN");
    private final String twilioPhoneNumber = System.getenv("TWILIO_PHONE_NUMBER");

    public void sendConfirmationSMS(Visit visit, String paymentId) {
        String msgBody = String.format(THANK_YOU_SMS,
                visit.getVehicle().getModel().getMake().getName(),
                visit.getVehicle().getModel().getName(),
                visit.getVehicle().getLicense(),
                paymentId);

        String to = "+359" + visit.getUser().getPhoneNumber().substring(1);
        Twilio.init(twilioSID, twilioToken);
        Message message = Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber(twilioPhoneNumber),
                        msgBody)
                .create();

        if (message.getStatus().equals(Message.Status.FAILED) || message.getStatus().equals(Message.Status.CANCELED)) {
            throw new IllegalStateException(ERROR_MESSAGE);
        };
    }
}
