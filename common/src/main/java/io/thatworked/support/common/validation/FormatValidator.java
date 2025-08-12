package io.thatworked.support.common.validation;

import java.util.regex.Pattern;
import java.util.UUID;

/**
 * Common format validation utilities for use across all microservices.
 * Provides consistent validation for formats like IP addresses, MAC addresses, etc.
 */
public class FormatValidator {
    
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
        "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );
    
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );
    
    // RFC 1123 compliant hostname pattern
    private static final Pattern HOSTNAME_PATTERN = Pattern.compile(
        "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*" +
        "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$"
    );
    
    /**
     * Validates IP address format.
     */
    public static boolean isValidIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        return IP_ADDRESS_PATTERN.matcher(ipAddress).matches();
    }
    
    /**
     * Validates MAC address format.
     * Delegates to MacAddressValidator for comprehensive format support.
     * @see MacAddressValidator#isValid(String)
     */
    public static boolean isValidMacAddress(String macAddress) {
        return MacAddressValidator.isValid(macAddress);
    }
    
    /**
     * Normalizes MAC address to standard format (ff:ff:ff:ff:ff:ff).
     * Delegates to MacAddressValidator for normalization.
     * @see MacAddressValidator#normalize(String)
     */
    public static String normalizeMacAddress(String macAddress) {
        return MacAddressValidator.normalize(macAddress);
    }
    
    /**
     * Normalizes MAC address to clean lowercase string for database storage.
     * Example: "AA:BB:CC:DD:EE:FF" becomes "aabbccddeeff"
     * Delegates to MacAddressValidator for normalization.
     * @see MacAddressValidator#normalizeForStorage(String)
     */
    public static String normalizeMacAddressForStorage(String macAddress) {
        return MacAddressValidator.normalizeForStorage(macAddress);
    }
    
    /**
     * Validates hostname format according to RFC 1123.
     * Hostnames can contain letters, numbers, hyphens, and dots.
     * Each label must start and end with an alphanumeric character.
     */
    public static boolean isValidHostname(String hostname) {
        if (hostname == null || hostname.trim().isEmpty()) {
            return false;
        }
        // Check maximum length (253 characters for full hostname)
        if (hostname.length() > 253) {
            return false;
        }
        // Check each label doesn't exceed 63 characters
        String[] labels = hostname.split("\\.");
        for (String label : labels) {
            if (label.length() > 63) {
                return false;
            }
        }
        return HOSTNAME_PATTERN.matcher(hostname).matches();
    }
    
    /**
     * Validates email format.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates UUID format.
     */
    public static boolean isValidUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Validates that a string is not empty after trimming.
     */
    public static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates that a string does not exceed maximum length.
     */
    public static boolean isWithinLength(String value, int maxLength) {
        return value == null || value.length() <= maxLength;
    }
    
    /**
     * Validates that a number is within range.
     */
    public static boolean isWithinRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a number is positive.
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Validates that a device has at least one valid address (IP or hostname).
     */
    public static boolean hasValidDeviceAddress(String ipAddress, String hostname) {
        boolean hasValidIp = ipAddress != null && !ipAddress.trim().isEmpty() && isValidIpAddress(ipAddress);
        boolean hasValidHostname = hostname != null && !hostname.trim().isEmpty() && isValidHostname(hostname);
        return hasValidIp || hasValidHostname;
    }
}