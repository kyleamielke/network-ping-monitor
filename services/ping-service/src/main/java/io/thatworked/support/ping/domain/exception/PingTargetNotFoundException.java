package io.thatworked.support.ping.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a ping target is not found.
 */
public class PingTargetNotFoundException extends PingDomainException {
    
    public PingTargetNotFoundException(UUID deviceId) {
        super("Ping target not found for device: " + deviceId);
    }
}