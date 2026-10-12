package com.example.scenarios.compliance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** Custom @Conditional: jurisdiction-specific beans, consumed optionally via ObjectProvider. */
class RegionConditionTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(GdprAuditTrail.class, CustomerProfileService.class);

    @Test
    void auditTrailIsAbsentOutsideEuAndUkAndServiceStillWorks() {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(GdprAuditTrail.class);
            CustomerProfileService service = context.getBean(CustomerProfileService.class);
            assertThat(service.load("c-1", "support-ticket").customerId()).isEqualTo("c-1");
            assertThat(service.isAuditing()).isFalse();
        });

        runner.withPropertyValues("app.region=apac")
                .run(context -> assertThat(context).doesNotHaveBean(GdprAuditTrail.class));
    }

    @Test
    void auditTrailRecordsPersonalDataAccessInTheEu() {
        runner.withPropertyValues("app.region=eu").run(context -> {
            context.getBean(CustomerProfileService.class).load("c-42", "marketing-export");
            assertThat(context.getBean(GdprAuditTrail.class).entries()).containsExactly("c-42:marketing-export");
        });
    }

    @Test
    void regionMatchIsCaseInsensitive() {
        runner.withPropertyValues("app.region=UK")
                .run(context -> assertThat(context).hasSingleBean(GdprAuditTrail.class));
    }
}
