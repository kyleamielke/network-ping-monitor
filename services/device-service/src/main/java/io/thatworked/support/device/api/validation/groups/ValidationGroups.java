package io.thatworked.support.device.api.validation.groups;

/**
 * Validation groups for different device operations.
 * Allows different validation rules for create vs update operations.
 */
public class ValidationGroups {
    
    /**
     * Validation group for device creation.
     * Used when all required fields must be present.
     */
    public interface Create {}
    
    /**
     * Validation group for device updates.
     * Used when fields are optional (partial updates).
     */
    public interface Update {}
    
    /**
     * Validation group for bulk operations.
     * May have relaxed validation rules.
     */
    public interface BulkOperation {}
    
    /**
     * Validation group for search operations.
     * Used for search criteria validation.
     */
    public interface Search {}
}