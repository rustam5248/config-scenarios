package com.example.scenarios.payments;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

/** Talks to the real payment provider. Only ever wired under the {@code prod} profile. */
public class LivePaymentGateway implements PaymentGateway {

    private final String baseUrl;
    private final String apiKey;
    private final Duration timeout;

    public LivePaymentGateway(String baseUrl, String apiKey, Duration timeout) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.timeout = timeout;
    }

    @Override
    public String name() {
        return "live";
    }

    @Override
    public PaymentResult charge(String orderId, BigDecimal amount, String currency) {
        // Real implementation: POST {baseUrl}/v1/charges with "Authorization: Bearer {apiKey}",
        // honouring {timeout}. Simulated here so the project runs offline.
        return new PaymentResult(name(), "live_" + UUID.randomUUID(), true);
    }

    public String baseUrl() {
        return baseUrl;
    }

    public Duration timeout() {
        return timeout;
    }
}
