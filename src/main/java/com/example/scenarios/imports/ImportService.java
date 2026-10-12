package com.example.scenarios.imports;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;

/**
 * SINGLETON that needs a new PROTOTYPE per call.
 *
 * <p>Injecting {@code CsvImportJob} directly would resolve it once, when this singleton is created,
 * and every import would reuse that one instance (the classic "prototype inside a singleton" trap;
 * see {@code ImportScopeTest}). {@link ObjectProvider#getObject()} asks the container each time.
 * Alternatives: a {@code @Lookup} method, or {@code jakarta.inject.Provider}.
 */
@Service
public class ImportService {

    private final ObjectProvider<CsvImportJob> jobs;

    public ImportService(ObjectProvider<CsvImportJob> jobs) {
        this.jobs = jobs;
    }

    public ImportSummary importCsv(String csv) {
        CsvImportJob job = jobs.getObject();
        return job.run(csv.lines().filter(Predicate.not(String::isBlank)).toList());
    }
}
