package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.service.PingTargetDomainService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for stopping ping monitoring.
 */
@Service
public class StopPingMonitoringUseCase {
    
    private final PingTargetDomainService pingTargetDomainService;
    
    public StopPingMonitoringUseCase(PingTargetDomainService pingTargetDomainService) {
        this.pingTargetDomainService = pingTargetDomainService;
    }
    
    public PingTargetDomain execute(StopPingMonitoringCommand command) {
        return pingTargetDomainService.stopMonitoring(command.getDeviceId());
    }
    
    public static class StopPingMonitoringCommand {
        private final UUID deviceId;
        
        public StopPingMonitoringCommand(UUID deviceId) {
            this.deviceId = deviceId;
        }
        
        public UUID getDeviceId() {
            return deviceId;
        }
    }
}