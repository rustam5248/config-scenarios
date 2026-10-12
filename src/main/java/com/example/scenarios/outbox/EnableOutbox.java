package com.example.scenarios.outbox;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Opt-in module switch in the style of {@code @EnableScheduling} / {@code @EnableCaching}.
 * Services that publish domain events add {@code @EnableOutbox}; the others pay nothing.
 * {@link OutboxImportSelector} reads {@link #store()} and imports the matching configuration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OutboxImportSelector.class)
public @interface EnableOutbox {

    OutboxStore store() default OutboxStore.JDBC;
}
