package com.example.scenarios.compliance;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom condition: register the annotated bean only when the service is deployed in one of the
 * given regions ({@code app.region}, case-insensitive, default {@code us}).
 * Use it for jurisdiction-specific behavior such as GDPR audit trails or data-residency rules.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(OnRegionCondition.class)
public @interface ConditionalOnRegion {

    String[] value();
}
