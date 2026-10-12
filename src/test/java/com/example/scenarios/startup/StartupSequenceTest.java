package com.example.scenarios.startup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The whole application (default "dev" profile): singletons with @DependsOn, then SmartLifecycle
 * beans by phase, then ApplicationRunners by @Order. The @Lazy engine waits until first use.
 */
@SpringBootTest
class StartupSequenceTest {

    @Autowired
    private StartupJournal journal;

    @Autowired
    private MonthlyReportService monthlyReports;

    @Test
    void startupMilestonesFollowTheConfiguredOrderAndLazyBeansWaitForFirstUse() {
        assertThat(journal.events()).containsSubsequence(
                "schema-migrated",
                "reference-data-loaded",
                "started:outbox-relay",
                "started:order-event-consumer",
                "runner:smoke-check",
                "runner:wiring-summary");
        assertThat(journal.events()).doesNotContain("reporting-engine-initialized");

        assertThat(monthlyReports.generate("2026-09")).isEqualTo("Monthly report for 2026-09");
        assertThat(journal.events()).endsWith("reporting-engine-initialized");
    }
}
