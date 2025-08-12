package io.thatworked.support.device.config.properties;

import lombok.Data;

/**
 * Feature flag configuration for enabling/disabling functionality.
 */
@Data
public class FeatureProperties {
    
    /**
     * Enable async event publishing
     */
    private boolean asyncEvents = true;
    
    /**
     * Enable device validation on updates
     */
    private boolean validateOnUpdate = true;
    
    /**
     * Enable duplicate IP detection
     */
    private boolean duplicateIpCheck = true;
    
    /**
     * Enable soft delete functionality
     */
    private boolean softDelete = false;
    
    /**
     * Enable audit logging
     */
    private boolean auditLogging = true;
    
    /**
     * Enable device import/export
     */
    private boolean importExport = false;
    
    /**
     * Enable bulk operations
     */
    private boolean bulkOperations = true;
    
    /**
     * Enable advanced search features
     */
    private boolean advancedSearch = true;
    
    /**
     * Enable device relationships
     */
    private boolean deviceRelationships = false;
    
    /**
     * Enable device tagging
     */
    private boolean tagging = false;
    
    /**
     * Enable rate limiting
     */
    private boolean rateLimiting = false;
    
    /**
     * Enable response compression
     */
    private boolean compression = true;
}