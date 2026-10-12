package com.example.scenarios.startup;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Stand-in for a DataSource: tables only exist once the schema migration has run. */
@Component
public class InMemoryDatabase {

    private final Map<String, String> countries = new ConcurrentHashMap<>();
    private volatile boolean migrated;

    void applyMigrations() {
        countries.put("DE", "Germany");
        countries.put("JP", "Japan");
        countries.put("US", "United States");
        migrated = true;
    }

    public Map<String, String> selectCountries() {
        if (!migrated) {
            throw new IllegalStateException("Table 'countries' does not exist: schema migration has not run yet");
        }
        return Map.copyOf(countries);
    }
}
