package io.thatworked.support.device.domain.exception;

/**
 * Exception thrown when attempting to create a device with duplicate unique fields.
 */
public class DuplicateDeviceDomainException extends DeviceDomainException {
    
    private final String field;
    private final String value;
    
    public DuplicateDeviceDomainException(String field, String value) {
        super("DUPLICATE_DEVICE", "Device already exists with " + field + ": " + value);
        this.field = field;
        this.value = value;
    }
    
    public String getField() {
        return field;
    }
    
    public String getValue() {
        return value;
    }
}