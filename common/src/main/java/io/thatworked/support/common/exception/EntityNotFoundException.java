package io.thatworked.support.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Common exception for when a requested entity cannot be found.
 * This provides a consistent way to handle "not found" scenarios across all services.
 */
public class EntityNotFoundException extends BaseServiceException {
    
    private static final String DEFAULT_ERROR_CODE = "ENTITY_NOT_FOUND";
    private static final int DEFAULT_HTTP_STATUS = HttpStatus.NOT_FOUND.value();
    
    private final String entityType;
    private final String entityId;
    
    public EntityNotFoundException(String entityType, String entityId) {
        super(DEFAULT_ERROR_CODE, 
              String.format("%s with identifier '%s' not found", entityType, entityId), 
              DEFAULT_HTTP_STATUS);
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public EntityNotFoundException(String entityType, String entityId, String customMessage) {
        super(DEFAULT_ERROR_CODE, customMessage, DEFAULT_HTTP_STATUS);
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public EntityNotFoundException(String entityType, String entityId, Throwable cause) {
        super(DEFAULT_ERROR_CODE, 
              String.format("%s with identifier '%s' not found", entityType, entityId), 
              DEFAULT_HTTP_STATUS, cause);
        this.entityType = entityType;
        this.entityId = entityId;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getEntityId() {
        return entityId;
    }
}