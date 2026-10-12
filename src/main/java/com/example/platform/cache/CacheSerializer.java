package com.example.platform.cache;

public interface CacheSerializer {

    byte[] serialize(Object value);

    <T> T deserialize(byte[] bytes, Class<T> type);

    String format();
}
