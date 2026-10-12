package com.example.scenarios.payments;

import java.math.BigDecimal;

public interface PaymentGateway {

    String name();

    PaymentResult charge(String orderId, BigDecimal amount, String currency);
}
