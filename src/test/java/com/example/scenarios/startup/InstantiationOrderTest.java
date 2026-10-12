package com.example.scenarios.startup;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** @DependsOn and SmartLifecycle phases, with registration order chosen to make the point. */
class InstantiationOrderTest {

    /** Same hidden dependency as ReferenceDataCache, but without @DependsOn. */
    static class ReferenceDataCacheWithoutDependsOn {

        ReferenceDataCacheWithoutDependsOn(InMemoryDatabase database) {
            database.selectCountries();
        }
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    void withoutDependsOnTheCacheIsCreatedBeforeTheMigrationAndStartupFails() {
        // Registered before SchemaMigrator; nothing tells the container about the real dependency.
        runner.withUserConfiguration(StartupJournal.class, InMemoryDatabase.class,
                        ReferenceDataCacheWithoutDependsOn.class, SchemaMigrator.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).rootCause().hasMessageContaining("schema migration has not run");
                });
    }

    @Test
    void dependsOnForcesTheMigrationFirstRegardlessOfRegistrationOrder() {
        runner.withUserConfiguration(StartupJournal.class, InMemoryDatabase.class,
                        ReferenceDataCache.class, SchemaMigrator.class)
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(StartupJournal.class).events())
                            .containsExactly("schema-migrated", "reference-data-loaded");
                    assertThat(context.getBean(ReferenceDataCache.class).countryName("JP")).isEqualTo("Japan");
                });
    }

    @Test
    void lifecyclePhasesStartTheConsumerLastAndStopItFirst() {
        // Registered consumer-first on purpose: only the phase values decide the order.
        runner.withUserConfiguration(StartupJournal.class, OrderEventConsumer.class, OutboxRelay.class)
                .run(context -> {
                    StartupJournal journal = context.getBean(StartupJournal.class);
                    context.close();
                    assertThat(journal.events()).containsExactly(
                            "started:outbox-relay",
                            "started:order-event-consumer",
                            "stopped:order-event-consumer",
                            "stopped:outbox-relay");
                });
    }
}
