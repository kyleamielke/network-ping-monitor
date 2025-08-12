package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.service.PingTargetDomainService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for starting ping monitoring.
 */
@Service
public class StartPingMonitoringUseCase {
    
    private final PingTargetDomainService pingTargetDomainService;
    
    public StartPingMonitoringUseCase(PingTargetDomainService pingTargetDomainService) {
        this.pingTargetDomainService = pingTargetDomainService;
    }
    
    public PingTargetDomain execute(StartPingMonitoringCommand command) {
        return pingTargetDomainService.startMonitoring(command.getDeviceId());
    }
    
    public static class StartPingMonitoringCommand {
        private final UUID deviceId;
        
        public StartPingMonitoringCommand(UUID deviceId) {
            this.deviceId = deviceId;
        }
        
        public UUID getDeviceId() {
            return deviceId;
        }
    }
}