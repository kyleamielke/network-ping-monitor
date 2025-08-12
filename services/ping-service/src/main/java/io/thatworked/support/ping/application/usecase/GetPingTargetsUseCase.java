package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingTargetQueryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case for retrieving ping targets.
 * Uses QueryPort for clean architecture separation.
 */
@Service
public class GetPingTargetsUseCase {
    
    private final PingTargetQueryPort pingTargetQueryPort;
    
    public GetPingTargetsUseCase(PingTargetQueryPort pingTargetQueryPort) {
        this.pingTargetQueryPort = pingTargetQueryPort;
    }
    
    public List<PingTargetDomain> getAllPingTargets() {
        return pingTargetQueryPort.findAll();
    }
    
    public List<PingTargetDomain> getAllActivePingTargets() {
        return pingTargetQueryPort.findAllActive();
    }
    
    public Optional<PingTargetDomain> getPingTarget(UUID deviceId) {
        return pingTargetQueryPort.findById(deviceId);
    }
    
    public List<PingTargetDomain> getPingTargetsByDeviceIds(List<UUID> deviceIds) {
        return pingTargetQueryPort.findByDeviceIds(deviceIds);
    }
}