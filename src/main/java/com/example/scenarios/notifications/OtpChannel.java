package com.example.scenarios.notifications;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Type-safe qualifier naming a <em>role</em> ("the channel used for one-time passwords"),
 * not an implementation. Moving OTP from SMS to another provider means moving this
 * annotation to a different sender class; {@link OtpService} does not change.
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Qualifier
public @interface OtpChannel {
}
