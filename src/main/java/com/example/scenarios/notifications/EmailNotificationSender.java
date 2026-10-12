package com.example.scenarios.notifications;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Default channel: {@code @Primary} wins whenever someone injects a single
 * {@code NotificationSender} without a qualifier. {@code @Order(1)} puts it first
 * when the senders are injected as a list.
 */
@Component
@Primary
@Order(1)
public class EmailNotificationSender implements NotificationSender {

    @Override
    public Channel channel() {
        return Channel.EMAIL;
    }

    @Override
    public DeliveryReceipt send(String recipient, String message) {
        // A real implementation would call SES / SMTP here.
        return new DeliveryReceipt(Channel.EMAIL, recipient, message);
    }
}
