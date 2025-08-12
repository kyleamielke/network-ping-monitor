package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.service.PingTargetDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for creating ping targets.
 */
@Service
public class CreatePingTargetUseCase {
    
    private final PingTargetDomainService pingTargetDomainService;
    private final StructuredLogger logger;
    
    public CreatePingTargetUseCase(PingTargetDomainService pingTargetDomainService,
                                  StructuredLoggerFactory structuredLoggerFactory) {
        this.pingTargetDomainService = pingTargetDomainService;
        this.logger = structuredLoggerFactory.getLogger(CreatePingTargetUseCase.class);
    }
    
    public PingTargetDomain execute(CreatePingTargetCommand command) {
        logger.with("deviceId", command.getDeviceId())
              .with("ipAddress", command.getIpAddress())
              .with("hostname", command.getHostname())
              .with("intervalSeconds", command.getIntervalSeconds())
              .info("Creating ping target");
              
        return pingTargetDomainService.createPingTarget(
            command.getDeviceId(),
            command.getIpAddress(),
            command.getHostname(),
            command.getIntervalSeconds()
        );
    }
    
    public static class CreatePingTargetCommand {
        private final UUID deviceId;
        private final String ipAddress;
        private final String hostname;
        private final int intervalSeconds;
        
        public CreatePingTargetCommand(UUID deviceId, String ipAddress, int intervalSeconds) {
            this(deviceId, ipAddress, null, intervalSeconds);
        }
        
        public CreatePingTargetCommand(UUID deviceId, String ipAddress, String hostname, int intervalSeconds) {
            this.deviceId = deviceId;
            this.ipAddress = ipAddress;
            this.hostname = hostname;
            this.intervalSeconds = intervalSeconds;
        }
        
        public UUID getDeviceId() {
            return deviceId;
        }
        
        public String getIpAddress() {
            return ipAddress;
        }
        
        public String getHostname() {
            return hostname;
        }
        
        public int getIntervalSeconds() {
            return intervalSeconds;
        }
    }
}