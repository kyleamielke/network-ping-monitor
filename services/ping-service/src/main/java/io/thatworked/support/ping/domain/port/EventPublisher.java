package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.PingResultDomain;
import io.thatworked.support.ping.domain.model.PingTargetDomain;

import java.util.UUID;

/**
 * Domain port for publishing domain events.
 */
public interface EventPublisher {
    
    void publishPingResult(PingResultDomain pingResult);
    
    void publishDeviceDown(UUID deviceId, String deviceName, String ipAddress);
    
    void publishDeviceRecovered(UUID deviceId, String deviceName, String ipAddress);
    
    void publishPingTargetStarted(PingTargetDomain pingTarget);
    
    void publishPingTargetStopped(PingTargetDomain pingTarget);
    
    void publishPingTargetIpUpdated(PingTargetDomain pingTarget, String oldIpAddress);
    
    void publishPingTargetAddressUpdated(PingTargetDomain pingTarget, String oldIpAddress, String oldHostname);
}