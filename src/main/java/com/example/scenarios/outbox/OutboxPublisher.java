package com.example.scenarios.outbox;

import java.time.Instant;

public class OutboxPublisher {

    private final OutboxStorage storage;

    public OutboxPublisher(OutboxStorage storage) {
        this.storage = storage;
    }

    public void publish(String aggregateId, String eventType, String payload) {
        storage.append(new OutboxMessage(aggregateId, eventType, payload, Instant.now()));
    }

    public String storageKind() {
        return storage.kind();
    }
}
