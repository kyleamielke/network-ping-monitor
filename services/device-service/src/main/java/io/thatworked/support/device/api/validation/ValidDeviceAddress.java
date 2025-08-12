package io.thatworked.support.device.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a device has at least one valid address (IP or hostname).
 * This is a class-level constraint that checks both ipAddress and hostname fields.
 */
@Documented
@Constraint(validatedBy = DeviceAddressValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDeviceAddress {
    String message() default "Device must have either a valid IP address or hostname";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}