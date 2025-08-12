package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.PingTargetDomain;

import java.util.UUID;

/**
 * Domain port for ping execution management.
 */
public interface PingExecutor {
    
    void startPing(PingTargetDomain pingTarget);
    
    void stopPing(UUID deviceId);
}