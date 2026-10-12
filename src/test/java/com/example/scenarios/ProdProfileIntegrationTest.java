package com.example.scenarios;

import com.example.platform.cache.CacheClient;
import com.example.scenarios.notifications.Channel;
import com.example.scenarios.notifications.NotificationRouter;
import com.example.scenarios.payments.LivePaymentGateway;
import com.example.scenarios.payments.PaymentGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End to end: activating "prod" loads application-prod.yml, which flips every environment-sensitive
 * decision at once (profile-gated gateway, property-gated cache backend and push channel).
 */
@SpringBootTest(properties = {"PAYMENTS_API_KEY=sk_test_integration", "REDIS_HOST=redis.test"})
@ActiveProfiles("prod")
class ProdProfileIntegrationTest {

    @Autowired
    private PaymentGateway paymentGateway;

    @Autowired
    private CacheClient cacheClient;

    @Autowired
    private NotificationRouter notificationRouter;

    @Test
    void prodProfileWiresProductionImplementations() {
        assertThat(paymentGateway).isInstanceOf(LivePaymentGateway.class);
        assertThat(((LivePaymentGateway) paymentGateway).baseUrl()).isEqualTo("https://api.payments.example.com");
        assertThat(cacheClient.backend()).isEqualTo("redis://redis.test:6379 (jackson)");
        assertThat(notificationRouter.availableChannels()).containsExactly(Channel.EMAIL, Channel.SMS, Channel.PUSH);
    }
}
