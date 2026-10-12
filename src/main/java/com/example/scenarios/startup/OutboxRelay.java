package com.example.scenarios.startup;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Background worker that ships outbox rows to the message broker.
 *
 * <p>{@link SmartLifecycle} beans start after ALL singletons are created, in ascending phase order,
 * and stop in descending phase order. The relay (phase 100) starts before the consumer (phase 200)
 * and stops after it, so events produced while the consumer drains its last messages still go out.
 */
@Component
public class OutboxRelay implements SmartLifecycle {

    public static final int PHASE = 100;

    private final StartupJournal journal;
    private volatile boolean running;

    public OutboxRelay(StartupJournal journal) {
        this.journal = journal;
    }

    @Override
    public void start() {
        journal.record("started:outbox-relay");
        running = true;
    }

    @Override
    public void stop() {
        journal.record("stopped:outbox-relay");
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPhase() {
        return PHASE;
    }
}
