package com.example.scenarios.compliance;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Arrays;

/**
 * Extending {@link SpringBootCondition} (rather than implementing {@code Condition} directly) means the
 * match/no-match message shows up in the condition evaluation report ({@code --debug}).
 */
class OnRegionCondition extends SpringBootCondition {

    static final String REGION_PROPERTY = "app.region";
    static final String DEFAULT_REGION = "us";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] regions = metadata.getAnnotations().get(ConditionalOnRegion.class).getStringArray("value");
        String current = context.getEnvironment().getProperty(REGION_PROPERTY, DEFAULT_REGION);

        boolean matches = Arrays.stream(regions).anyMatch(region -> region.equalsIgnoreCase(current));
        String message = "region '" + current + "' " + (matches ? "is" : "is not") + " in " + Arrays.toString(regions);
        return matches ? ConditionOutcome.match(message) : ConditionOutcome.noMatch(message);
    }
}
