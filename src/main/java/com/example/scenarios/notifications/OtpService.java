package com.example.scenarios.notifications;

import org.springframework.stereotype.Service;

/** Two-factor codes must always go over the channel tagged {@link OtpChannel}, never the @Primary default. */
@Service
public class OtpService {

    private final NotificationSender otpSender;

    public OtpService(@OtpChannel NotificationSender otpSender) {
        this.otpSender = otpSender;
    }

    public DeliveryReceipt sendCode(String phoneNumber, String code) {
        return otpSender.send(phoneNumber, "Your verification code is " + code);
    }
}
