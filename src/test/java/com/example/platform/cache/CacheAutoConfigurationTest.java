package com.example.platform.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;

/** @ConditionalOnProperty, @ConditionalOnMissingBean and @ConditionalOnClass in a library auto-configuration. */
class CacheAutoConfigurationTest {

    public record ProductPrice(String sku, long cents) implements Serializable {
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CacheAutoConfiguration.class));

    @Test
    void defaultsToInMemoryWhenNothingIsConfigured() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(CacheClient.class);
            assertThat(context.getBean(CacheClient.class)).isInstanceOf(InMemoryCacheClient.class);
        });
    }

    @Test
    void switchesToRedisWithJacksonWhenTypeIsRedis() {
        runner.withPropertyValues("platform.cache.type=redis", "platform.cache.redis.host=cache.internal")
                .run(context -> {
                    assertThat(context).hasSingleBean(CacheClient.class);
                    CacheClient cache = context.getBean(CacheClient.class);
                    assertThat(cache).isInstanceOf(RedisCacheClient.class);
                    assertThat(cache.backend()).isEqualTo("redis://cache.internal:6379 (jackson)");

                    cache.put("sku-1", new ProductPrice("sku-1", 1999));
                    assertThat(cache.get("sku-1", ProductPrice.class)).contains(new ProductPrice("sku-1", 1999));
                });
    }

    @Test
    void fallsBackToJdkSerializationWhenJacksonIsNotOnTheClasspath() {
        runner.withClassLoader(new FilteredClassLoader(ObjectMapper.class))
                .withPropertyValues("platform.cache.type=redis")
                .run(context -> {
                    assertThat(context.getBean(CacheSerializer.class)).isInstanceOf(JdkCacheSerializer.class);
                    CacheClient cache = context.getBean(CacheClient.class);
                    assertThat(cache.backend()).isEqualTo("redis://localhost:6379 (jdk)");

                    cache.put("sku-2", new ProductPrice("sku-2", 450));
                    assertThat(cache.get("sku-2", ProductPrice.class)).contains(new ProductPrice("sku-2", 450));
                });
    }

    @Test
    void applicationDefinedClientMakesTheDefaultsBackOff() {
        runner.withBean("tenantAwareCache", CacheClient.class, InMemoryCacheClient::new)
                .withPropertyValues("platform.cache.type=redis")
                .run(context -> assertThat(context.getBeansOfType(CacheClient.class)).containsOnlyKeys("tenantAwareCache"));
    }
}
