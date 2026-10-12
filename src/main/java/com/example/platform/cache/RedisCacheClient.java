package com.example.platform.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stand-in for a Redis-backed client so the project runs without a Redis server.
 * Values cross a serialization boundary exactly as they would over the wire,
 * which is why this client needs a {@link CacheSerializer}.
 */
public class RedisCacheClient implements CacheClient {

    private final String host;
    private final int port;
    private final CacheSerializer serializer;
    private final Map<String, byte[]> remoteStore = new ConcurrentHashMap<>(); // simulates the Redis server

    public RedisCacheClient(String host, int port, CacheSerializer serializer) {
        this.host = host;
        this.port = port;
        this.serializer = serializer;
    }

    @Override
    public void put(String key, Object value) {
        remoteStore.put(key, serializer.serialize(value));
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(remoteStore.get(key)).map(bytes -> serializer.deserialize(bytes, type));
    }

    @Override
    public String backend() {
        return "redis://" + host + ":" + port + " (" + serializer.format() + ")";
    }
}
