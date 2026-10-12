package com.example.scenarios.payments;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

/**
 * Environment-specific wiring with profile <em>expressions</em>.
 *
 * <p>Load tests run with the full prod configuration (same pools, timeouts, cache) but must never
 * charge real cards, so they activate {@code prod,load-test}:
 * <pre>
 *   profiles active          gateway
 *   (none) / dev / test      sandbox
 *   prod                     live
 *   prod, load-test          sandbox
 * </pre>
 * The two expressions are exact complements, so exactly one gateway exists in every environment.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PaymentProperties.class)
public class PaymentConfig {

    @Bean
    @Profile("prod & !load-test")
    PaymentGateway livePaymentGateway(PaymentProperties properties) {
        // Fail fast at startup instead of on the first customer checkout.
        if (!StringUtils.hasText(properties.apiKey())) {
            throw new IllegalStateException("payments.api-key is required when the 'prod' profile is active");
        }
        return new LivePaymentGateway(properties.baseUrl(), properties.apiKey(), properties.timeout());
    }

    @Bean
    @Profile("!prod | load-test")
    PaymentGateway sandboxPaymentGateway() {
        return new SandboxPaymentGateway();
    }
}
