package com.example.scenarios.notifications;

import org.springframework.stereotype.Service;

/** Doesn't care which channel is used: gets the {@code @Primary} sender (email). */
@Service
public class ReceiptService {

    private final NotificationSender sender;

    public ReceiptService(NotificationSender sender) {
        this.sender = sender;
    }

    public DeliveryReceipt sendReceipt(String customerEmail, String orderId) {
        return sender.send(customerEmail, "Thanks for your order " + orderId);
    }
}
