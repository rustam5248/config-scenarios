package com.example.scenarios.startup;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * Message-queue consumer. It must not take traffic until everything it relies on is running, and on
 * shutdown it must stop first so in-flight messages finish while downstream components are still up.
 * A higher phase than {@link OutboxRelay} gives exactly that: start last, stop first.
 */
@Component
public class OrderEventConsumer implements SmartLifecycle {

    public static final int PHASE = 200;

    private final StartupJournal journal;
    private volatile boolean running;

    public OrderEventConsumer(StartupJournal journal) {
        this.journal = journal;
    }

    @Override
    public void start() {
        journal.record("started:order-event-consumer");
        running = true;
    }

    @Override
    public void stop() {
        journal.record("stopped:order-event-consumer");
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
