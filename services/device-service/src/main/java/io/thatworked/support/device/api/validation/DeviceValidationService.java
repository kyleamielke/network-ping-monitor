package io.thatworked.support.device.api.validation;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.common.validation.FormatValidator;
import io.thatworked.support.common.validation.MacAddressValidator;
import io.thatworked.support.device.infrastructure.entity.Device;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceValidationService {
    
    private final StructuredLogger logger;

    public DeviceValidationService(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DeviceValidationService.class);
    }

    public void validateDevice(Device device) {
        List<String> errors = new ArrayList<>();

        // Check required fields
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null");
        }

        if (!StringUtils.hasText(device.getName())) {
            errors.add("Device name is required");
        }

        if (!StringUtils.hasText(device.getIpAddress())) {
            errors.add("IP address is required");
        } else if (!FormatValidator.isValidIpAddress(device.getIpAddress())) {
            errors.add("Invalid IP address format: " + device.getIpAddress());
        }

        // Validate MAC address if provided
        if (StringUtils.hasText(device.getMacAddress()) && !MacAddressValidator.isValid(device.getMacAddress())) {
            errors.add("Invalid MAC address format: " + device.getMacAddress());
        }

        // Validate device type - now allows any type for flexibility
        if (StringUtils.hasText(device.getType()) && !DeviceTypeValidator.isValidDeviceType(device.getType())) {
            errors.add("Invalid device type: " + device.getType());
        }

        // Removed specific device type requirements to allow custom types
        // Users can define their own device types with their own rules

        // If there are any validation errors, throw an exception
        if (!errors.isEmpty()) {
            String errorMessage = String.join(", ", errors);
            logger.with("validationErrors", errors)
                    .with("deviceName", device.getName())
                    .with("deviceType", device.getType())
                    .warn("Device validation failed");
            throw new IllegalArgumentException("Device validation failed: " + errorMessage);
        }
    }
}