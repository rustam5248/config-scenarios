package com.example.scenarios.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/** Populates the request-scoped {@link RequestContext} from incoming headers. */
@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    public static final String CORRELATION_HEADER = "X-Correlation-Id";
    public static final String TENANT_HEADER = "X-Tenant-Id";

    private final RequestContext requestContext; // scoped proxy

    public RequestContextInterceptor(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (StringUtils.hasText(correlationId)) {
            requestContext.setCorrelationId(correlationId);
        }
        String tenantId = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(tenantId)) {
            requestContext.setTenantId(tenantId);
        }
        response.setHeader(CORRELATION_HEADER, requestContext.getCorrelationId());
        return true;
    }
}
