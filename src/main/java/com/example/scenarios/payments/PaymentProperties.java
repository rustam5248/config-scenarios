package com.example.scenarios.payments;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

/** Values differ per environment: see application.yml and application-prod.yml. */
@ConfigurationProperties("payments")
public record PaymentProperties(
        @DefaultValue("https://sandbox.payments.example.com") String baseUrl,
        String apiKey,
        @DefaultValue("5s") Duration timeout) {
}
