package com.example.scenarios.formatting;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;

/**
 * Custom THREAD scope: {@link DecimalFormat} is not thread-safe and is relatively costly to build,
 * so each worker thread gets its own instance and reuses it.
 *
 * <p>Caveats: {@code SimpleThreadScope} never runs destruction callbacks, and with virtual threads
 * (one thread per task) the reuse benefit disappears. Prefer immutable, thread-safe types such as
 * {@code java.time.format.DateTimeFormatter} when one exists; this pattern is for legacy APIs.
 */
@Component
@Scope(value = ThreadScopeConfig.THREAD_SCOPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CurrencyFormatter {

    private final String instanceId = UUID.randomUUID().toString();
    private final DecimalFormat format = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    public String format(BigDecimal amount, String currency) {
        return format.format(amount) + " " + currency;
    }

    public String instanceId() {
        return instanceId;
    }
}
