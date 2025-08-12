package io.thatworked.support.ping.infrastructure.event;

import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.application.service.VirtualThreadPingService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PingEventHandler {
    private final StructuredLogger logger;
    private final VirtualThreadPingService virtualThreadPingService;
    
    public PingEventHandler(StructuredLoggerFactory structuredLoggerFactory,
                           VirtualThreadPingService virtualThreadPingService) {
        this.logger = structuredLoggerFactory.getLogger(PingEventHandler.class);
        this.virtualThreadPingService = virtualThreadPingService;
    }

    @EventListener
    public void handlePingTargetStarted(PingTargetStartedEvent event) {
        try {
            logger.with("deviceId", event.getPingTarget().getDeviceId()).info("Ping target started event received");
            virtualThreadPingService.startMonitoring(event.getPingTarget());
        } catch (Exception e) {
            logger.with("deviceId", event.getPingTarget().getDeviceId())
                  .with("method", "handlePingTargetStarted")
                  .error("Error handling ping target started event", e);
        }
    }

    @EventListener
    public void handlePingTargetStopped(PingTargetStoppedEvent event) {
        try {
            logger.with("deviceId", event.getDeviceId()).info("Ping target stopped event received");
            virtualThreadPingService.stopMonitoring(event.getDeviceId());
        } catch (Exception e) {
            logger.with("deviceId", event.getDeviceId())
                  .with("method", "handlePingTargetStopped")
                  .error("Error handling ping target stopped event", e);
        }
    }
    
    @EventListener
    public void handlePingTargetIpUpdated(PingTargetIpUpdatedEvent event) {
        try {
            logger.with("deviceId", event.getPingTarget().getDeviceId())
                  .with("oldIpAddress", event.getOldIpAddress())
                  .with("newIpAddress", event.getPingTarget().getIpAddress())
                  .info("Ping target IP updated event received");
            
            // Stop monitoring with old IP and restart with new IP
            virtualThreadPingService.stopMonitoring(event.getPingTarget().getDeviceId());
            virtualThreadPingService.startMonitoring(event.getPingTarget());
        } catch (Exception e) {
            logger.with("deviceId", event.getPingTarget().getDeviceId())
                  .with("method", "handlePingTargetIpUpdated")
                  .error("Error handling ping target IP updated event", e);
        }
    }

}