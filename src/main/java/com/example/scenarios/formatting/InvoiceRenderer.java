package com.example.scenarios.formatting;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/** Singleton used from many worker threads; the injected formatter proxy resolves per thread. */
@Service
public class InvoiceRenderer {

    private final CurrencyFormatter formatter;

    public InvoiceRenderer(CurrencyFormatter formatter) {
        this.formatter = formatter;
    }

    public String renderTotal(BigDecimal total, String currency) {
        return "Total: " + formatter.format(total, currency);
    }

    /** Exposed so tests can see which per-thread instance served the call. */
    public String formatterInstanceId() {
        return formatter.instanceId();
    }
}
