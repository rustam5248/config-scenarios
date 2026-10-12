package com.example.scenarios.payments;

import java.math.BigDecimal;

/**
 * Deterministic fake for dev, test and load-test runs: approves up to 1,000.00 and declines above,
 * so both happy and unhappy checkout paths can be exercised without real money.
 */
public class SandboxPaymentGateway implements PaymentGateway {

    static final BigDecimal DECLINE_ABOVE = new BigDecimal("1000.00");

    @Override
    public String name() {
        return "sandbox";
    }

    @Override
    public PaymentResult charge(String orderId, BigDecimal amount, String currency) {
        boolean approved = amount.compareTo(DECLINE_ABOVE) <= 0;
        return new PaymentResult(name(), "sbx_" + orderId, approved);
    }
}
