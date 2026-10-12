package com.example.scenarios.notifications;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** Multiple implementations of one interface: @Primary, custom qualifier, ordered list injection. */
class NotificationWiringTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(
                    EmailNotificationSender.class, SmsNotificationSender.class, PushNotificationSender.class,
                    NotificationRouter.class, OtpService.class, ReceiptService.class);

    @Test
    void unqualifiedSingleInjectionGetsThePrimaryImplementation() {
        runner.run(context -> {
            assertThat(context.getBean(NotificationSender.class)).isInstanceOf(EmailNotificationSender.class);
            assertThat(context.getBean(ReceiptService.class).sendReceipt("ana@example.com", "A-1001").channel())
                    .isEqualTo(Channel.EMAIL);
        });
    }

    @Test
    void customQualifierPinsOtpCodesToSms() {
        runner.run(context -> assertThat(context.getBean(OtpService.class).sendCode("+15550100", "123456").channel())
                .isEqualTo(Channel.SMS));
    }

    @Test
    void listInjectionFollowsOrderAndContainsOnlyRegisteredImplementations() {
        runner.run(context -> {
            assertThat(context).doesNotHaveBean(PushNotificationSender.class);
            assertThat(context.getBean(NotificationRouter.class).availableChannels())
                    .containsExactly(Channel.EMAIL, Channel.SMS);
        });

        runner.withPropertyValues("notifications.push.enabled=true").run(context ->
                assertThat(context.getBean(NotificationRouter.class).availableChannels())
                        .containsExactly(Channel.EMAIL, Channel.SMS, Channel.PUSH));
    }

    @Test
    void routerFallsBackToHighestPriorityChannelWhenPreferredOneIsDisabled() {
        runner.run(context -> {
            DeliveryReceipt receipt = context.getBean(NotificationRouter.class)
                    .send(Channel.PUSH, "device-77", "Your parcel has shipped");
            assertThat(receipt.channel()).isEqualTo(Channel.EMAIL);
        });
    }
}
