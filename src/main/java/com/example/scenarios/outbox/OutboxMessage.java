package com.example.scenarios.outbox;

import java.time.Instant;

public record OutboxMessage(String aggregateId, String eventType, String payload, Instant createdAt) {
}
