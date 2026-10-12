package com.example.scenarios.outbox;

import org.springframework.context.annotation.Bean;

/**
 * Configuration classes reachable only through {@link OutboxImportSelector}.
 *
 * <p>They are intentionally NOT annotated with {@code @Configuration}: component scanning would
 * otherwise register both storage variants regardless of what {@code @EnableOutbox} asked for.
 * Classes brought in via {@code @Import} / {@code ImportSelector} are processed as (lite-mode)
 * configuration classes without the annotation.
 */
public final class OutboxConfigurations {

    private OutboxConfigurations() {
    }

    static class Core {

        @Bean
        OutboxPublisher outboxPublisher(OutboxStorage storage) {
            return new OutboxPublisher(storage);
        }
    }

    static class Jdbc {

        @Bean
        OutboxStorage jdbcOutboxStorage() {
            return new JdbcOutboxStorage();
        }
    }

    static class InMemory {

        @Bean
        OutboxStorage inMemoryOutboxStorage() {
            return new InMemoryOutboxStorage();
        }
    }
}
