package com.example.scenarios.payments;

public record PaymentResult(String gateway, String transactionId, boolean approved) {
}
