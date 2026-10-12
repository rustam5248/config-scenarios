package com.example.scenarios.imports;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Prototype scope, and the "prototype injected into a singleton" trap. */
class ImportScopeTest {

    /** What NOT to do: the prototype is resolved once, when this singleton is created. */
    static class NaiveImportService {

        private final CsvImportJob job;

        NaiveImportService(CsvImportJob job) {
            this.job = job;
        }

        ImportSummary importCsv(String csv) {
            return job.run(csv.lines().toList());
        }
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    void containerReturnsANewPrototypeOnEveryRequest() {
        runner.withUserConfiguration(CsvImportJob.class).run(context ->
                assertThat(context.getBean(CsvImportJob.class)).isNotSameAs(context.getBean(CsvImportJob.class)));
    }

    @Test
    void objectProviderGivesEachImportItsOwnJob() {
        runner.withUserConfiguration(CsvImportJob.class, ImportService.class).run(context -> {
            ImportService imports = context.getBean(ImportService.class);

            ImportSummary first = imports.importCsv("sku-1,Mug,9.99\nbroken-line\n");
            ImportSummary second = imports.importCsv("sku-2,Plate,4.50\nsku-3,Bowl,6.00\n");

            assertThat(first.jobId()).isNotEqualTo(second.jobId());
            assertThat(first.imported()).isEqualTo(1);
            assertThat(first.rejectedLines()).containsExactly("broken-line");
            assertThat(second.imported()).isEqualTo(2);
            assertThat(second.rejectedLines()).isEmpty();
        });
    }

    @Test
    void injectingThePrototypeDirectlyFreezesOneInstanceForTheSingletonsLifetime() {
        runner.withUserConfiguration(CsvImportJob.class, NaiveImportService.class).run(context -> {
            NaiveImportService naive = context.getBean(NaiveImportService.class);
            naive.importCsv("sku-1,Mug,9.99");

            assertThatThrownBy(() -> naive.importCsv("sku-2,Plate,4.50"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("single-use");
        });
    }
}
