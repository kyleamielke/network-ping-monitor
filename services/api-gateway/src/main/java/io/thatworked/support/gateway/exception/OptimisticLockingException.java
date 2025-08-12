package io.thatworked.support.gateway.exception;

/**
 * Exception thrown when an optimistic locking conflict is detected.
 * This occurs when attempting to update a resource with a stale version.
 */
public class OptimisticLockingException extends RuntimeException {
    
    private final Long expectedVersion;
    private final Long currentVersion;
    
    public OptimisticLockingException(String resourceType, String resourceId, Long expectedVersion, Long currentVersion) {
        super(String.format("Optimistic locking failure for %s %s: Expected version %d but current version is %d. Please refresh and retry.",
                resourceType, resourceId, expectedVersion, currentVersion));
        this.expectedVersion = expectedVersion;
        this.currentVersion = currentVersion;
    }
    
    public Long getExpectedVersion() {
        return expectedVersion;
    }
    
    public Long getCurrentVersion() {
        return currentVersion;
    }
}