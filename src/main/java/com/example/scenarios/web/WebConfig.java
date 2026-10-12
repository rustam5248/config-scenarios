package com.example.scenarios.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

    private final RequestContextInterceptor requestContextInterceptor;

    public WebConfig(RequestContextInterceptor requestContextInterceptor) {
        this.requestContextInterceptor = requestContextInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestContextInterceptor);
    }
}
