package com.example.platform.cache;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Default for local development and single-instance deployments. */
public class InMemoryCacheClient implements CacheClient {

    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Object value) {
        store.put(key, value);
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(store.get(key)).map(type::cast);
    }

    @Override
    public String backend() {
        return "in-memory";
    }
}
