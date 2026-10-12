package com.example.scenarios.notifications;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Feature-flagged implementation: push is only registered when
 * {@code notifications.push.enabled=true} (it is on in the prod profile).
 * When the bean is absent, {@link NotificationRouter}'s injected list simply has
 * one element fewer; nothing else needs to know.
 */
@Component
@Order(3)
@ConditionalOnProperty(prefix = "notifications.push", name = "enabled", havingValue = "true")
public class PushNotificationSender implements NotificationSender {

    @Override
    public Channel channel() {
        return Channel.PUSH;
    }

    @Override
    public DeliveryReceipt send(String recipient, String message) {
        // A real implementation would call FCM / APNs here.
        return new DeliveryReceipt(Channel.PUSH, recipient, message);
    }
}
