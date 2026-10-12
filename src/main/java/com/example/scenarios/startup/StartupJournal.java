package com.example.scenarios.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Records startup/shutdown milestones so the ordering guarantees can be asserted in tests. */
@Component
public class StartupJournal {

    private static final Logger log = LoggerFactory.getLogger(StartupJournal.class);

    private final List<String> events = new CopyOnWriteArrayList<>();

    public void record(String event) {
        events.add(event);
        log.info("startup-journal: {}", event);
    }

    public List<String> events() {
        return List.copyOf(events);
    }
}
