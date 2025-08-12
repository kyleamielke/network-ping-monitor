package io.thatworked.support.gateway.exception;

/**
 * Exception thrown when invalid input is provided to an operation.
 */
public class InvalidInputException extends RuntimeException {
    
    private final String field;
    private final Object value;
    private final String constraint;
    
    public InvalidInputException(String field, Object value, String constraint) {
        super(String.format("Invalid value for field '%s': %s. %s", field, value, constraint));
        this.field = field;
        this.value = value;
        this.constraint = constraint;
    }
    
    public InvalidInputException(String message) {
        super(message);
        this.field = null;
        this.value = null;
        this.constraint = null;
    }
    
    public String getField() {
        return field;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getConstraint() {
        return constraint;
    }
}