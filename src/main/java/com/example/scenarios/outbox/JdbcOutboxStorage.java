package com.example.scenarios.outbox;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Production storage. A real implementation would {@code INSERT INTO outbox(...)} through the
 * service's JdbcTemplate inside the business transaction; simulated here so the project needs no database.
 */
public class JdbcOutboxStorage implements OutboxStorage {

    private final List<OutboxMessage> table = new CopyOnWriteArrayList<>();

    @Override
    public String kind() {
        return "jdbc";
    }

    @Override
    public void append(OutboxMessage message) {
        table.add(message);
    }

    @Override
    public List<OutboxMessage> pending() {
        return List.copyOf(table);
    }
}
