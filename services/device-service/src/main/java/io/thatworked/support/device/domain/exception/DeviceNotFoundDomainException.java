package io.thatworked.support.device.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a device is not found.
 */
public class DeviceNotFoundDomainException extends DeviceDomainException {
    
    private final UUID deviceId;
    
    public DeviceNotFoundDomainException(UUID deviceId) {
        super("DEVICE_NOT_FOUND", "Device not found with ID: " + deviceId);
        this.deviceId = deviceId;
    }
    
    public DeviceNotFoundDomainException(String field, String value) {
        super("DEVICE_NOT_FOUND", "Device not found with " + field + ": " + value);
        this.deviceId = null;
    }
    
    public UUID getDeviceId() {
        return deviceId;
    }
}