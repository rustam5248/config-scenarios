package com.example.platform.cache;

import java.util.Optional;

/** Cache abstraction shipped by an internal "platform" starter and consumed by product services. */
public interface CacheClient {

    void put(String key, Object value);

    <T> Optional<T> get(String key, Class<T> type);

    /** Human-readable description of the backing store, e.g. {@code in-memory} or {@code redis://host:6379 (jackson)}. */
    String backend();
}
