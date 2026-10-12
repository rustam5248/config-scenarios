package com.example.scenarios;

import com.example.scenarios.outbox.EnableOutbox;
import com.example.scenarios.outbox.OutboxStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. Component scanning covers {@code com.example.scenarios.*}.
 * The shared "platform" cache module lives in {@code com.example.platform} and is
 * deliberately NOT scanned: it arrives through auto-configuration, like a company starter would.
 */
@SpringBootApplication
@EnableOutbox(store = OutboxStore.IN_MEMORY)
public class ScenariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScenariosApplication.class, args);
    }
}
