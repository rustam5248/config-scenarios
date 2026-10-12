package com.example.scenarios.web;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SINGLETON that reads REQUEST-scoped state. The injected {@link RequestContext} is a proxy, so each
 * call sees the correlation id and tenant of whichever request is running on the current thread.
 */
@Component
public class AuditLogger {

    private final RequestContext requestContext;
    private final List<String> entries = new CopyOnWriteArrayList<>();

    public AuditLogger(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public String record(String action) {
        String entry = "[" + requestContext.getCorrelationId() + "][" + requestContext.getTenantId() + "] " + action;
        entries.add(entry);
        return entry;
    }

    public List<String> entries() {
        return List.copyOf(entries);
    }
}
