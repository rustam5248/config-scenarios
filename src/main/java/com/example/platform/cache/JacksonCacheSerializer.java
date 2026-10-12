package com.example.platform.cache;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;

/** Preferred serializer: only registered when Jackson is on the classpath. */
public class JacksonCacheSerializer implements CacheSerializer {

    private final ObjectMapper objectMapper;

    public JacksonCacheSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String format() {
        return "jackson";
    }
}
