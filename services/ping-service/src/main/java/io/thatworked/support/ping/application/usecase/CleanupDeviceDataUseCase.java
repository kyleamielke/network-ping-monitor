package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.service.PingMonitoringDomainService;
import io.thatworked.support.ping.domain.service.PingTargetDomainService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for cleaning up device data when a device is deleted.
 */
@Service
public class CleanupDeviceDataUseCase {
    
    private final PingTargetDomainService pingTargetDomainService;
    private final PingMonitoringDomainService pingMonitoringDomainService;
    
    public CleanupDeviceDataUseCase(PingTargetDomainService pingTargetDomainService,
                                   PingMonitoringDomainService pingMonitoringDomainService) {
        this.pingTargetDomainService = pingTargetDomainService;
        this.pingMonitoringDomainService = pingMonitoringDomainService;
    }
    
    public void execute(CleanupDeviceDataCommand command) {
        UUID deviceId = command.getDeviceId();
        
        // Delete ping target (this will stop monitoring if active)
        pingTargetDomainService.deletePingTarget(deviceId);
        
        // Clean up ping results and alert state
        pingMonitoringDomainService.cleanupDeviceData(deviceId);
    }
    
    public static class CleanupDeviceDataCommand {
        private final UUID deviceId;
        
        public CleanupDeviceDataCommand(UUID deviceId) {
            this.deviceId = deviceId;
        }
        
        public UUID getDeviceId() {
            return deviceId;
        }
    }
}