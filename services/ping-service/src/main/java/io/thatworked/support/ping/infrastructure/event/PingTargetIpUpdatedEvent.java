package io.thatworked.support.ping.infrastructure.event;

import io.thatworked.support.ping.domain.PingTarget;

/**
 * Event published when a ping target's IP address is updated.
 */
public class PingTargetIpUpdatedEvent {
    private final PingTarget pingTarget;
    private final String oldIpAddress;
    
    public PingTargetIpUpdatedEvent(PingTarget pingTarget, String oldIpAddress) {
        this.pingTarget = pingTarget;
        this.oldIpAddress = oldIpAddress;
    }
    
    public PingTarget getPingTarget() {
        return pingTarget;
    }
    
    public String getOldIpAddress() {
        return oldIpAddress;
    }
}