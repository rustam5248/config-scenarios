package com.example.platform.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Library-style wiring for the platform cache. Registered through
 * {@code META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports}, so it is
 * processed <em>after</em> every application-defined configuration class. That ordering is what makes
 * {@code @ConditionalOnMissingBean} reliable: if the service defines its own {@link CacheClient},
 * these defaults back off.
 *
 * <p>Conditions used:
 * <ul>
 *   <li>{@code @ConditionalOnProperty} picks the backend from {@code platform.cache.type}. The two
 *       backends use mutually exclusive values, so the result never depends on method order.</li>
 *   <li>{@code @ConditionalOnMissingBean} lets the application override any default.</li>
 *   <li>{@code @ConditionalOnClass} / {@code @ConditionalOnMissingClass} choose a serializer depending
 *       on whether Jackson is on the classpath. Conditions are read from bytecode metadata, so the
 *       Jackson configuration class is never loaded when Jackson is absent.</li>
 * </ul>
 */
@AutoConfiguration(afterName = "org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration")
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CacheClient.class)
    @ConditionalOnProperty(prefix = "platform.cache", name = "type", havingValue = "redis")
    public CacheClient redisCacheClient(CacheProperties properties, CacheSerializer serializer) {
        return new RedisCacheClient(properties.redis().host(), properties.redis().port(), serializer);
    }

    @Bean
    @ConditionalOnMissingBean(CacheClient.class)
    @ConditionalOnProperty(prefix = "platform.cache", name = "type", havingValue = "in-memory", matchIfMissing = true)
    public CacheClient inMemoryCacheClient() {
        return new InMemoryCacheClient();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "com.fasterxml.jackson.databind.ObjectMapper")
    static class JacksonSerializerConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheSerializer.class)
        CacheSerializer jacksonCacheSerializer(ObjectProvider<ObjectMapper> objectMapper) {
            // Reuse Boot's configured ObjectMapper when the app has one (web apps do).
            return new JacksonCacheSerializer(objectMapper.getIfAvailable(ObjectMapper::new));
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("com.fasterxml.jackson.databind.ObjectMapper")
    static class JdkSerializerConfiguration {

        @Bean
        @ConditionalOnMissingBean(CacheSerializer.class)
        CacheSerializer jdkCacheSerializer() {
            return new JdkCacheSerializer();
        }
    }
}
