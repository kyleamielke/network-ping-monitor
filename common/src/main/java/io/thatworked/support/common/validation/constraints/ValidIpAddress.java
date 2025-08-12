package io.thatworked.support.common.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for IP addresses (IPv4 and IPv6).
 * Validates that the field contains a properly formatted IP address.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpAddressValidator.class)
@Documented
public @interface ValidIpAddress {
    String message() default "Invalid IP address format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}