package io.thatworked.support.device.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeviceTypeValidator.class)
@Documented
public @interface ValidDeviceType {
    String message() default "Invalid device type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}