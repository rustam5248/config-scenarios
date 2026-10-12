package com.example.scenarios.http;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/** Configuration class processing: full mode (CGLIB-proxied) vs lite mode. */
class HttpClientConfigTest {

    /** Same code as HttpClientConfig, but with proxyBeanMethods = false (bean names differ to avoid clashes). */
    @Configuration(proxyBeanMethods = false)
    static class LiteModeHttpClientConfig {

        @Bean
        ConnectionPool liteConnectionPool() {
            return new ConnectionPool(50);
        }

        @Bean
        InventoryClient liteInventoryClient() {
            return new InventoryClient(liteConnectionPool());
        }

        @Bean
        ShippingClient liteShippingClient() {
            return new ShippingClient(liteConnectionPool());
        }
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    void fullModeInterceptsInterBeanCallsSoBothClientsShareTheSingletonPool() {
        runner.withUserConfiguration(HttpClientConfig.class).run(context -> {
            ConnectionPool registered = context.getBean(ConnectionPool.class);
            assertThat(context.getBean(InventoryClient.class).pool()).isSameAs(registered);
            assertThat(context.getBean(ShippingClient.class).pool()).isSameAs(registered);
            // The configuration bean itself is a CGLIB subclass.
            assertThat(context.getBean(HttpClientConfig.class).getClass()).isNotEqualTo(HttpClientConfig.class);
        });
    }

    @Test
    void liteModeTreatsInterBeanCallsAsPlainJavaCalls() {
        runner.withUserConfiguration(LiteModeHttpClientConfig.class).run(context -> {
            ConnectionPool registered = context.getBean(ConnectionPool.class);
            ConnectionPool inventoryPool = context.getBean(InventoryClient.class).pool();
            ConnectionPool shippingPool = context.getBean(ShippingClient.class).pool();

            assertThat(inventoryPool).isNotSameAs(registered);
            assertThat(shippingPool).isNotSameAs(registered).isNotSameAs(inventoryPool);
            assertThat(context.getBean(LiteModeHttpClientConfig.class).getClass()).isEqualTo(LiteModeHttpClientConfig.class);
        });
    }
}
