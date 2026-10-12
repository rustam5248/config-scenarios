package com.example.scenarios.outbox;

public enum OutboxStore {
    /** Rows written in the same database transaction as the business change (production). */
    JDBC,
    /** Kept in memory; for local runs and tests. */
    IN_MEMORY
}
