package com.example.scenarios.startup;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Loads reference data from the database at startup.
 *
 * <p>Its constructor needs {@link InMemoryDatabase}, but nothing ties it to {@link SchemaMigrator}:
 * the real dependency (tables must exist) is invisible to the container. Without
 * {@code @DependsOn}, creation order falls back to registration order; component scanning typically
 * finds {@code ReferenceDataCache} before {@code SchemaMigrator}, so the load would fail, and even when
 * it happens to work it is an accident of class names, not a guarantee.
 * {@code @DependsOn} forces the migrator to be fully initialized first (and destroyed after this bean).
 * Spring Boot's Flyway/Liquibase auto-configuration sets up the same dependency for JPA and JDBC beans.
 */
@Component
@DependsOn(SchemaMigrator.BEAN_NAME)
public class ReferenceDataCache {

    private final InMemoryDatabase database;
    private final StartupJournal journal;
    private Map<String, String> countries = Map.of();

    public ReferenceDataCache(InMemoryDatabase database, StartupJournal journal) {
        this.database = database;
        this.journal = journal;
    }

    @PostConstruct
    void load() {
        countries = database.selectCountries();
        journal.record("reference-data-loaded");
    }

    public String countryName(String isoCode) {
        return countries.get(isoCode);
    }
}
