package io.thatworked.support.device.api.validation;

import io.thatworked.support.common.validation.FormatValidator;
import io.thatworked.support.device.api.dto.request.DeviceCreateRequest;
import io.thatworked.support.device.api.dto.request.DeviceUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates that a device has at least one valid address (IP or hostname).
 */
public class DeviceAddressValidator implements ConstraintValidator<ValidDeviceAddress, Object> {

    @Override
    public void initialize(ValidDeviceAddress constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let other constraints handle null checking
        }

        String ipAddress = null;
        String hostname = null;

        // Extract fields based on the object type
        if (value instanceof DeviceCreateRequest) {
            DeviceCreateRequest request = (DeviceCreateRequest) value;
            ipAddress = request.getIpAddress();
            hostname = request.getHostname();
        } else if (value instanceof DeviceUpdateRequest) {
            DeviceUpdateRequest request = (DeviceUpdateRequest) value;
            ipAddress = request.getIpAddress();
            hostname = request.getHostname();
            
            // For updates, if both address fields are null, the device already has an address
            // and we're not updating it, so validation should pass
            if (ipAddress == null && hostname == null) {
                return true;
            }
        } else {
            // Unknown type, let it pass
            return true;
        }

        // Check if at least one valid address is provided
        boolean hasValidAddress = FormatValidator.hasValidDeviceAddress(ipAddress, hostname);
        
        if (!hasValidAddress) {
            // Customize error message
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Device must have either a valid IP address or a valid hostname"
            ).addConstraintViolation();
        }
        
        return hasValidAddress;
    }
}