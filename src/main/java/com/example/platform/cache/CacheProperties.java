package com.example.platform.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * <pre>
 * platform.cache.type=in-memory | redis
 * platform.cache.redis.host=...
 * platform.cache.redis.port=6379
 * </pre>
 */
@ConfigurationProperties("platform.cache")
public record CacheProperties(
        @DefaultValue("in-memory") String type,
        @DefaultValue Redis redis) {

    public record Redis(
            @DefaultValue("localhost") String host,
            @DefaultValue("6379") int port) {
    }
}
