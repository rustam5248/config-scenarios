package com.example.scenarios.startup;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Application runners execute after the context is refreshed and lifecycle beans are started,
 * in {@code @Order} sequence. A failing runner aborts startup, which makes this a cheap self-test.
 */
@Component
@Order(1)
public class SmokeCheckRunner implements ApplicationRunner {

    private final ReferenceDataCache referenceData;
    private final StartupJournal journal;

    public SmokeCheckRunner(ReferenceDataCache referenceData, StartupJournal journal) {
        this.referenceData = referenceData;
        this.journal = journal;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (referenceData.countryName("DE") == null) {
            throw new IllegalStateException("Reference data missing after startup");
        }
        journal.record("runner:smoke-check");
    }
}
