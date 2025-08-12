package io.thatworked.support.common.validation.constraints;

import io.thatworked.support.common.validation.MacAddressValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * JSR-303 validator implementation for MAC addresses.
 * Delegates to MacAddressValidator for actual validation logic.
 */
public class MacAddressValidatorImpl implements ConstraintValidator<ValidMacAddress, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Null values are considered valid - use @NotNull/@NotBlank for required fields
        if (value == null) {
            return true;
        }
        
        return MacAddressValidator.isValid(value);
    }
}