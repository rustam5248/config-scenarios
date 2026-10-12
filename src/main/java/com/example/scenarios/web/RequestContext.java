package com.example.scenarios.web;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

/**
 * REQUEST scope: one instance per HTTP request, holding the caller's correlation id and tenant.
 *
 * <p>{@code @RequestScope} defaults to {@code proxyMode = TARGET_CLASS}. Singletons such as
 * {@link AuditLogger} receive a CGLIB proxy; every method call on it is routed to the instance
 * bound to the <em>current</em> request. Without the proxy, injecting this into a singleton would
 * fail at startup because no request is active then.
 */
@Component
@RequestScope
public class RequestContext {

    public static final String DEFAULT_TENANT = "public";

    private String correlationId = UUID.randomUUID().toString();
    private String tenantId = DEFAULT_TENANT;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
