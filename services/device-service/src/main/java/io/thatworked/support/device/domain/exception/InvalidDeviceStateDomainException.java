package io.thatworked.support.device.domain.exception;

/**
 * Exception thrown when a device operation is invalid for the current state.
 */
public class InvalidDeviceStateDomainException extends DeviceDomainException {
    
    public InvalidDeviceStateDomainException(String message) {
        super("INVALID_DEVICE_STATE", message);
    }
}