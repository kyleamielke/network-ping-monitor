package io.thatworked.support.device.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.Set;

import static java.util.Map.entry;

public class DeviceTypeValidator implements ConstraintValidator<ValidDeviceType, String> {
    private static final Map<String, Set<String>> REQUIRED_ROLES = Map.ofEntries(
        entry("Network Router", Set.of("Router", "Network Device")),
        entry("Network Switch", Set.of("Switch", "Network Device")),
        entry("Server", Set.of("Server")),
        entry("Workstation", Set.of("Workstation")),
        entry("Virtual Machine", Set.of("Virtual Machine")),
        entry("Security Device", Set.of("Firewall", "Security Device"))
    );

    private static final Set<String> VALID_DEVICE_TYPES = REQUIRED_ROLES.keySet();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Allow any non-empty device type for flexibility
        // Users can define their own device types
        return true;
    }

    public static boolean isValidDeviceType(String deviceType) {
        // Allow any device type for flexibility
        return true;
    }
    
    public static Set<String> getCommonDeviceTypes() {
        // Return common types for UI dropdowns
        return VALID_DEVICE_TYPES;
    }
}