package io.thatworked.support.device.api.exception.constants;

/**
 * Error message constants for the device service.
 */
public final class ErrorConstants {
    
    private ErrorConstants() {
        throw new IllegalStateException("Constants class");
    }
    
    // Error Messages
    public static final String DEVICE_NOT_FOUND = "Device not found with id: %s";
    public static final String DEVICE_NAME_REQUIRED = "Device name is required";
    public static final String INVALID_IP_ADDRESS = "Invalid IP address format";
    public static final String INVALID_MAC_ADDRESS = "Invalid MAC address format";
    public static final String DUPLICATE_IP_ADDRESS = "Device with IP address %s already exists";
}