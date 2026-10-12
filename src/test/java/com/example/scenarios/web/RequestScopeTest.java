package com.example.scenarios.web;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.scenarios.web.RequestContextInterceptor.CORRELATION_HEADER;
import static com.example.scenarios.web.RequestContextInterceptor.TENANT_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Request scope behind a scoped proxy, read by a singleton. */
@SpringBootTest
@AutoConfigureMockMvc
class RequestScopeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestContext requestContext;

    @Test
    void eachRequestGetsItsOwnContextEvenThroughASingleton() throws Exception {
        mockMvc.perform(get("/orders/42").header(CORRELATION_HEADER, "corr-1").header(TENANT_HEADER, "acme"))
                .andExpect(status().isOk())
                .andExpect(header().string(CORRELATION_HEADER, "corr-1"))
                .andExpect(jsonPath("$.tenant").value("acme"))
                .andExpect(jsonPath("$.audit").value("[corr-1][acme] viewed order 42"));

        // Second request carries no headers: a brand-new RequestContext with defaults, nothing leaks over.
        mockMvc.perform(get("/orders/43"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenant").value(RequestContext.DEFAULT_TENANT))
                .andExpect(jsonPath("$.correlationId").value(not("corr-1")));
    }

    @Test
    void singletonsReceiveAScopedProxyRatherThanTheTargetInstance() {
        assertThat(AopUtils.isAopProxy(requestContext)).isTrue();
    }
}
