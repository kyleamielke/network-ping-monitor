package io.thatworked.support.device.application.util;

import lombok.experimental.UtilityClass;

/**
 * Device-specific utility methods.
 */
@UtilityClass
public class DeviceServiceUtils {
    
    private static final int MAX_DEVICE_NAME_LENGTH = 100;
    
    /**
     * Sanitizes a device name by trimming whitespace and limiting length.
     *
     * @param name the device name to sanitize
     * @return sanitized device name or null if input is null
     */
    public static String sanitizeDeviceName(String name) {
        if (name == null) {
            return null;
        }
        // Remove leading/trailing whitespace and limit length
        String sanitized = name.trim();
        return sanitized.length() > MAX_DEVICE_NAME_LENGTH 
            ? sanitized.substring(0, MAX_DEVICE_NAME_LENGTH) 
            : sanitized;
    }
}