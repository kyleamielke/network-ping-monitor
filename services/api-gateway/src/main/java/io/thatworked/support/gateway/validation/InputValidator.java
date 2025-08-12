package io.thatworked.support.gateway.validation;

import io.thatworked.support.common.validation.FormatValidator;

/**
 * Input validation wrapper for GraphQL mutations.
 * Delegates to common FormatValidator for consistency across services.
 * Handles format validation only - business rules are validated by services.
 */
public class InputValidator {
    
    /**
     * Validates IP address format.
     * @see FormatValidator#isValidIpAddress(String)
     */
    public static boolean isValidIpAddress(String ipAddress) {
        return FormatValidator.isValidIpAddress(ipAddress);
    }
    
    /**
     * Validates MAC address format.
     * Supports multiple formats including colon, dash, dot, Cisco, and others.
     * @see FormatValidator#isValidMacAddress(String)
     */
    public static boolean isValidMacAddress(String macAddress) {
        return FormatValidator.isValidMacAddress(macAddress);
    }
    
    /**
     * Normalizes MAC address to standard format.
     * @see FormatValidator#normalizeMacAddress(String)
     */
    public static String normalizeMacAddress(String macAddress) {
        return FormatValidator.normalizeMacAddress(macAddress);
    }
    
    /**
     * Validates email format.
     * @see FormatValidator#isValidEmail(String)
     */
    public static boolean isValidEmail(String email) {
        return FormatValidator.isValidEmail(email);
    }
    
    /**
     * Validates UUID format.
     * @see FormatValidator#isValidUuid(String)
     */
    public static boolean isValidUuid(String uuid) {
        return FormatValidator.isValidUuid(uuid);
    }
    
    /**
     * Validates that a string is not empty after trimming.
     * @see FormatValidator#isNotBlank(String)
     */
    public static boolean isNotBlank(String value) {
        return FormatValidator.isNotBlank(value);
    }
    
    /**
     * Validates that a string does not exceed maximum length.
     * @see FormatValidator#isWithinLength(String, int)
     */
    public static boolean isWithinLength(String value, int maxLength) {
        return FormatValidator.isWithinLength(value, maxLength);
    }
    
    /**
     * Validates that a number is within range.
     * @see FormatValidator#isWithinRange(int, int, int)
     */
    public static boolean isWithinRange(int value, int min, int max) {
        return FormatValidator.isWithinRange(value, min, max);
    }
    
    /**
     * Validates that a number is positive.
     * @see FormatValidator#isPositive(int)
     */
    public static boolean isPositive(int value) {
        return FormatValidator.isPositive(value);
    }
    
    /**
     * Validates hostname format.
     * @see FormatValidator#isValidHostname(String)
     */
    public static boolean isValidHostname(String hostname) {
        return FormatValidator.isValidHostname(hostname);
    }
}