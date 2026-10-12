package com.example.scenarios.startup;

import com.example.platform.cache.CacheClient;
import com.example.scenarios.compliance.CustomerProfileService;
import com.example.scenarios.notifications.NotificationRouter;
import com.example.scenarios.outbox.OutboxPublisher;
import com.example.scenarios.payments.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/** Logs which implementations were wired for the active environment. Runs after {@link SmokeCheckRunner}. */
@Component
@Order(2)
public class WiringSummaryRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WiringSummaryRunner.class);

    private final Environment environment;
    private final PaymentGateway paymentGateway;
    private final CacheClient cacheClient;
    private final NotificationRouter notificationRouter;
    private final CustomerProfileService customerProfiles;
    private final OutboxPublisher outboxPublisher;
    private final StartupJournal journal;

    public WiringSummaryRunner(Environment environment, PaymentGateway paymentGateway, CacheClient cacheClient,
                               NotificationRouter notificationRouter, CustomerProfileService customerProfiles,
                               OutboxPublisher outboxPublisher, StartupJournal journal) {
        this.environment = environment;
        this.paymentGateway = paymentGateway;
        this.cacheClient = cacheClient;
        this.notificationRouter = notificationRouter;
        this.customerProfiles = customerProfiles;
        this.outboxPublisher = outboxPublisher;
        this.journal = journal;
    }

    @Override
    public void run(ApplicationArguments args) {
        String[] active = environment.getActiveProfiles();
        String profiles = active.length > 0 ? Arrays.toString(active) : Arrays.toString(environment.getDefaultProfiles()) + " (default)";
        log.info("""

                ---- wiring summary --------------------------------
                profiles      : {}
                payments      : {}
                cache         : {}
                notifications : {}
                gdpr audit    : {}
                outbox store  : {}
                ----------------------------------------------------""",
                profiles,
                paymentGateway.name(),
                cacheClient.backend(),
                notificationRouter.availableChannels(),
                customerProfiles.isAuditing() ? "on" : "off",
                outboxPublisher.storageKind());
        journal.record("runner:wiring-summary");
    }
}
