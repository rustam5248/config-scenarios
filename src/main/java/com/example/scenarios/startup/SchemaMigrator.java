package com.example.scenarios.startup;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/** Plays the role Flyway/Liquibase play in a real service: migrate the schema during startup. */
@Component(SchemaMigrator.BEAN_NAME)
public class SchemaMigrator {

    public static final String BEAN_NAME = "schemaMigrator";

    private final InMemoryDatabase database;
    private final StartupJournal journal;

    public SchemaMigrator(InMemoryDatabase database, StartupJournal journal) {
        this.database = database;
        this.journal = journal;
    }

    @PostConstruct
    void migrate() {
        database.applyMigrations();
        journal.record("schema-migrated");
    }
}
