package com.example.scenarios.notifications;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@OtpChannel
@Order(2)
public class SmsNotificationSender implements NotificationSender {

    @Override
    public Channel channel() {
        return Channel.SMS;
    }

    @Override
    public DeliveryReceipt send(String recipient, String message) {
        // A real implementation would call an SMS provider's API here.
        return new DeliveryReceipt(Channel.SMS, recipient, message);
    }
}
