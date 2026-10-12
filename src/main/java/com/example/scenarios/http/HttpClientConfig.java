package com.example.scenarios.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FULL-mode configuration ({@code proxyBeanMethods = true}, the default for {@code @Configuration}).
 *
 * <p>Spring subclasses this class with CGLIB. The two direct calls to {@code connectionPool()} below
 * are intercepted and return the container's singleton, so both clients share ONE pool.
 *
 * <p>In LITE mode ({@code proxyBeanMethods = false}, or {@code @Bean} methods on a plain
 * {@code @Component}) the same code would be a normal Java call: each client would get its own new
 * pool, and neither would be the pool registered as a bean ({@code HttpClientConfigTest} shows this).
 * Lite mode starts faster and works with GraalVM native images; when using it, take dependencies as
 * method parameters ({@code inventoryClient(ConnectionPool pool)}) instead of calling other
 * {@code @Bean} methods.
 */
@Configuration(proxyBeanMethods = true)
public class HttpClientConfig {

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(50);
    }

    @Bean
    public InventoryClient inventoryClient() {
        return new InventoryClient(connectionPool());
    }

    @Bean
    public ShippingClient shippingClient() {
        return new ShippingClient(connectionPool());
    }
}
