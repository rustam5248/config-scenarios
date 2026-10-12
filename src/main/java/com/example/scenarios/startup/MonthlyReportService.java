package com.example.scenarios.startup;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * {@code @Lazy} on the class alone is not enough: an eager singleton that injects a lazy bean forces
 * it to be created at startup anyway. {@code @Lazy} at the injection point gives this service a
 * lazy-resolution proxy; the real {@link ReportingEngine} is created on the first method call.
 */
@Service
public class MonthlyReportService {

    private final ReportingEngine engine;

    public MonthlyReportService(@Lazy ReportingEngine engine) {
        this.engine = engine;
    }

    public String generate(String month) {
        return engine.render(month);
    }
}
