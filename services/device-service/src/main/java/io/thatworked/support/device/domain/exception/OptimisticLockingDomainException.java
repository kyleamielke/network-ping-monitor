package io.thatworked.support.device.domain.exception;

/**
 * Exception thrown when an optimistic locking conflict is detected during update.
 */
public class OptimisticLockingDomainException extends RuntimeException {
    
    private final String resourceType;
    private final String resourceId;
    private final Long expectedVersion;
    private final Long currentVersion;
    
    public OptimisticLockingDomainException(String resourceType, String resourceId, 
                                           Long expectedVersion, Long currentVersion) {
        super(String.format("Optimistic locking failure for %s %s: Expected version %d but current version is %d",
                resourceType, resourceId, expectedVersion, currentVersion));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.expectedVersion = expectedVersion;
        this.currentVersion = currentVersion;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public String getResourceId() {
        return resourceId;
    }
    
    public Long getExpectedVersion() {
        return expectedVersion;
    }
    
    public Long getCurrentVersion() {
        return currentVersion;
    }
}