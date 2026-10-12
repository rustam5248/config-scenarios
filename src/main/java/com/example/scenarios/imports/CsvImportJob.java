package com.example.scenarios.imports;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PROTOTYPE scope: a stateful, single-use unit of work (counters, rejected rows, a job id).
 * Sharing one instance between imports, or between threads, would mix up their results,
 * so the container hands out a fresh instance every time one is requested.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CsvImportJob {

    private static final int EXPECTED_COLUMNS = 3; // sku,name,price

    private final String jobId = UUID.randomUUID().toString();
    private final List<String> rejected = new ArrayList<>();
    private int imported;
    private boolean used;

    public ImportSummary run(List<String> lines) {
        if (used) {
            throw new IllegalStateException("CsvImportJob " + jobId + " is single-use; obtain a new instance per import");
        }
        used = true;
        for (String line : lines) {
            if (line.split(",", -1).length == EXPECTED_COLUMNS) {
                imported++;
            } else {
                rejected.add(line);
            }
        }
        return new ImportSummary(jobId, imported, List.copyOf(rejected));
    }

    public String jobId() {
        return jobId;
    }
}
