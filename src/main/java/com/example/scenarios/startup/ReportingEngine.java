package com.example.scenarios.startup;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Expensive to initialise (think: compiling report templates, loading fonts) and needed only when
 * someone actually runs a report. {@code @Lazy} defers creation from startup to first use, keeping
 * startup time and readiness probes fast.
 */
@Component
@Lazy
public class ReportingEngine {

    private final StartupJournal journal;

    public ReportingEngine(StartupJournal journal) {
        this.journal = journal;
    }

    @PostConstruct
    void initialise() {
        journal.record("reporting-engine-initialized");
    }

    public String render(String month) {
        return "Monthly report for " + month;
    }
}
