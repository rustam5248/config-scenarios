package com.example.scenarios.formatting;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/** Custom scope registered through a static BeanFactoryPostProcessor @Bean. */
class ThreadScopeTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(ThreadScopeConfig.class, CurrencyFormatter.class, InvoiceRenderer.class);

    @Test
    void sameThreadReusesItsFormatterAndOtherThreadsGetTheirOwn() {
        runner.run(context -> {
            InvoiceRenderer renderer = context.getBean(InvoiceRenderer.class);
            String first = renderer.formatterInstanceId();
            String again = renderer.formatterInstanceId();

            ExecutorService worker = Executors.newSingleThreadExecutor();
            try {
                String otherThread = worker.submit(renderer::formatterInstanceId).get(5, TimeUnit.SECONDS);
                assertThat(again).isEqualTo(first);
                assertThat(otherThread).isNotEqualTo(first);
            } finally {
                worker.shutdownNow();
            }

            assertThat(renderer.renderTotal(new BigDecimal("1234.5"), "EUR")).isEqualTo("Total: 1,234.50 EUR");
        });
    }
}
