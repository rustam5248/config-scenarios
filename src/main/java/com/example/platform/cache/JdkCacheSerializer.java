package com.example.platform.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;

/**
 * Fallback serializer for services that don't ship Jackson. Values must be {@link java.io.Serializable}.
 * Only ever reads bytes this service wrote itself; never use Java deserialization on untrusted input.
 */
public class JdkCacheSerializer implements CacheSerializer {

    @Override
    public byte[] serialize(Object value) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
            out.writeObject(value);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return buffer.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            return type.cast(in.readObject());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String format() {
        return "jdk";
    }
}
