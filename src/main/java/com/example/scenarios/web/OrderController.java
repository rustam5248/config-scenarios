package com.example.scenarios.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final AuditLogger auditLogger;
    private final RequestContext requestContext;

    public OrderController(AuditLogger auditLogger, RequestContext requestContext) {
        this.auditLogger = auditLogger;
        this.requestContext = requestContext;
    }

    @GetMapping("/{orderId}")
    public Map<String, String> getOrder(@PathVariable("orderId") String orderId) {
        String auditEntry = auditLogger.record("viewed order " + orderId);
        return Map.of(
                "orderId", orderId,
                "tenant", requestContext.getTenantId(),
                "correlationId", requestContext.getCorrelationId(),
                "audit", auditEntry);
    }
}
