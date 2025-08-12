package io.thatworked.support.common.validation.constraints;

import io.thatworked.support.common.validation.FormatValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * JSR-303 validator implementation for IP addresses.
 * Delegates to FormatValidator for actual validation logic.
 */
public class IpAddressValidator implements ConstraintValidator<ValidIpAddress, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid - use @NotNull/@NotBlank for required fields
        if (value == null) {
            return true;
        }
        
        return FormatValidator.isValidIpAddress(value);
    }
}