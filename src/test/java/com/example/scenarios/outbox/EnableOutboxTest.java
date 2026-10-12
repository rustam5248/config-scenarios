package com.example.scenarios.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @Enable-style module with an ImportSelector. The "service" classes below are plain classes (no
 * @Configuration) so component scanning never picks them up; @EnableOutbox's meta-annotated @Import
 * is enough for Spring to process them as configuration.
 */
class EnableOutboxTest {

    @EnableOutbox
    static class ProductionService {
    }

    @EnableOutbox(store = OutboxStore.IN_MEMORY)
    static class LocalService {
    }

    static class ServiceWithoutEvents {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    void defaultStoreIsJdbc() {
        runner.withUserConfiguration(ProductionService.class).run(context -> {
            assertThat(context).hasSingleBean(OutboxStorage.class);
            OutboxPublisher publisher = context.getBean(OutboxPublisher.class);
            assertThat(publisher.storageKind()).isEqualTo("jdbc");

            publisher.publish("order-1", "OrderPlaced", "{\"total\":42}");
            assertThat(context.getBean(OutboxStorage.class).pending()).hasSize(1);
        });
    }

    @Test
    void annotationAttributeSelectsInMemoryStore() {
        runner.withUserConfiguration(LocalService.class).run(context -> {
            assertThat(context).hasSingleBean(OutboxStorage.class);
            assertThat(context.getBean(OutboxPublisher.class).storageKind()).isEqualTo("in-memory");
        });
    }

    @Test
    void servicesThatDoNotOptInGetNoOutboxBeans() {
        runner.withUserConfiguration(ServiceWithoutEvents.class).run(context -> {
            assertThat(context).doesNotHaveBean(OutboxPublisher.class);
            assertThat(context).doesNotHaveBean(OutboxStorage.class);
        });
    }
}
