package com.example.scenarios.notifications;

/**
 * One contract, several implementations (email, SMS, push).
 * Consumers pick an implementation in one of three ways:
 * <ul>
 *   <li>inject {@code NotificationSender} and get the {@code @Primary} one ({@link ReceiptService});</li>
 *   <li>inject with a qualifier to pin a role to an implementation ({@link OtpService});</li>
 *   <li>inject {@code List<NotificationSender>} and choose at runtime ({@link NotificationRouter}).</li>
 * </ul>
 */
public interface NotificationSender {

    Channel channel();

    DeliveryReceipt send(String recipient, String message);
}
