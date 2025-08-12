package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingResultDomain;
import io.thatworked.support.ping.domain.service.PingMonitoringDomainService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for processing ping results.
 */
@Service
public class ProcessPingResultUseCase {
    
    private final PingMonitoringDomainService pingMonitoringDomainService;
    
    public ProcessPingResultUseCase(PingMonitoringDomainService pingMonitoringDomainService) {
        this.pingMonitoringDomainService = pingMonitoringDomainService;
    }
    
    public void execute(ProcessPingResultCommand command) {
        PingResultDomain pingResult = PingResultDomain.create(
            command.getDeviceId(),
            command.getIpAddress(),
            command.isSuccess(),
            command.getResponseTime(),
            command.getTimestamp()
        );
        
        pingMonitoringDomainService.processPingResult(pingResult);
    }
    
    public static class ProcessPingResultCommand {
        private final UUID deviceId;
        private final String ipAddress;
        private final boolean success;
        private final Long responseTime;
        private final Instant timestamp;
        
        public ProcessPingResultCommand(UUID deviceId, String ipAddress, boolean success, 
                                      Long responseTime, Instant timestamp) {
            this.deviceId = deviceId;
            this.ipAddress = ipAddress;
            this.success = success;
            this.responseTime = responseTime;
            this.timestamp = timestamp;
        }
        
        public UUID getDeviceId() {
            return deviceId;
        }
        
        public String getIpAddress() {
            return ipAddress;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Long getResponseTime() {
            return responseTime;
        }
        
        public Instant getTimestamp() {
            return timestamp;
        }
    }
}