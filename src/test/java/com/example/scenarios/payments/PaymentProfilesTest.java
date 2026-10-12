package com.example.scenarios.payments;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/** Environment-specific wiring with @Profile expressions. */
class PaymentProfilesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(PaymentConfig.class);

    private ApplicationContextRunner withProfiles(String... profiles) {
        return runner.withInitializer(context -> context.getEnvironment().setActiveProfiles(profiles));
    }

    @Test
    void noProfileOrDevUsesTheSandbox() {
        runner.run(context -> assertThat(context.getBean(PaymentGateway.class)).isInstanceOf(SandboxPaymentGateway.class));
        withProfiles("dev").run(context -> assertThat(context.getBean(PaymentGateway.class)).isInstanceOf(SandboxPaymentGateway.class));
    }

    @Test
    void prodUsesTheLiveGatewayWithProdSettings() {
        withProfiles("prod")
                .withPropertyValues("payments.api-key=sk_test_123", "payments.base-url=https://api.payments.example.com", "payments.timeout=2s")
                .run(context -> {
                    assertThat(context).hasSingleBean(PaymentGateway.class);
                    LivePaymentGateway gateway = (LivePaymentGateway) context.getBean(PaymentGateway.class);
                    assertThat(gateway.baseUrl()).isEqualTo("https://api.payments.example.com");
                    assertThat(gateway.timeout()).isEqualTo(Duration.ofSeconds(2));
                });
    }

    @Test
    void prodWithoutApiKeyFailsAtStartup() {
        withProfiles("prod").run(context -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("payments.api-key");
        });
    }

    @Test
    void loadTestOnTopOfProdNeverChargesRealCards() {
        withProfiles("prod", "load-test").run(context -> {
            assertThat(context).hasSingleBean(PaymentGateway.class);
            assertThat(context.getBean(PaymentGateway.class)).isInstanceOf(SandboxPaymentGateway.class);
        });
    }

    @Test
    void sandboxDeclinesLargeAmountsSoUnhappyPathsCanBeTested() {
        SandboxPaymentGateway sandbox = new SandboxPaymentGateway();
        assertThat(sandbox.charge("o-1", new BigDecimal("99.90"), "EUR").approved()).isTrue();
        assertThat(sandbox.charge("o-2", new BigDecimal("1500.00"), "EUR").approved()).isFalse();
    }
}
