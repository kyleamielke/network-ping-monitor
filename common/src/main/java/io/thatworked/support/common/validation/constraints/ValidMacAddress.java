package io.thatworked.support.common.validation.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation for MAC addresses.
 * Supports multiple formats including colon, dash, dot, Cisco, and others.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MacAddressValidatorImpl.class)
@Documented
public @interface ValidMacAddress {
    String message() default "Invalid MAC address format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}