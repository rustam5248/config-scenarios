package com.example.scenarios.formatting;

import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

/**
 * Registers a custom {@code "thread"} scope.
 *
 * <p>{@link CustomScopeConfigurer} is a {@code BeanFactoryPostProcessor}: it must run before any
 * regular bean is created. Declaring the {@code @Bean} method {@code static} lets Spring call it
 * without instantiating this configuration class first; a non-static method would force early
 * creation of the configuration class and Spring logs a warning about it.
 */
@Configuration(proxyBeanMethods = false)
public class ThreadScopeConfig {

    public static final String THREAD_SCOPE = "thread";

    @Bean
    static CustomScopeConfigurer threadScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        configurer.addScope(THREAD_SCOPE, new SimpleThreadScope());
        return configurer;
    }
}
