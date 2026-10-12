package com.example.scenarios.outbox;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryOutboxStorage implements OutboxStorage {

    private final List<OutboxMessage> messages = new CopyOnWriteArrayList<>();

    @Override
    public String kind() {
        return "in-memory";
    }

    @Override
    public void append(OutboxMessage message) {
        messages.add(message);
    }

    @Override
    public List<OutboxMessage> pending() {
        return List.copyOf(messages);
    }
}
