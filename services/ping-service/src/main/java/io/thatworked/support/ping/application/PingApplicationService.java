package io.thatworked.support.ping.application;

import io.thatworked.support.ping.application.usecase.*;
import io.thatworked.support.ping.domain.model.PingTargetDomain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for ping operations.
 * Orchestrates use cases and provides transaction boundaries.
 */
@Service
@Transactional
public class PingApplicationService {
    
    private final CreatePingTargetUseCase createPingTargetUseCase;
    private final StartPingMonitoringUseCase startPingMonitoringUseCase;
    private final StopPingMonitoringUseCase stopPingMonitoringUseCase;
    private final GetPingTargetsUseCase getPingTargetsUseCase;
    private final ProcessPingResultUseCase processPingResultUseCase;
    private final CleanupDeviceDataUseCase cleanupDeviceDataUseCase;
    private final UpdatePingTargetIpAddressUseCase updatePingTargetIpAddressUseCase;
    private final UpdatePingTargetAddressUseCase updatePingTargetAddressUseCase;
    
    public PingApplicationService(CreatePingTargetUseCase createPingTargetUseCase,
                                 StartPingMonitoringUseCase startPingMonitoringUseCase,
                                 StopPingMonitoringUseCase stopPingMonitoringUseCase,
                                 GetPingTargetsUseCase getPingTargetsUseCase,
                                 ProcessPingResultUseCase processPingResultUseCase,
                                 CleanupDeviceDataUseCase cleanupDeviceDataUseCase,
                                 UpdatePingTargetIpAddressUseCase updatePingTargetIpAddressUseCase,
                                 UpdatePingTargetAddressUseCase updatePingTargetAddressUseCase) {
        this.createPingTargetUseCase = createPingTargetUseCase;
        this.startPingMonitoringUseCase = startPingMonitoringUseCase;
        this.stopPingMonitoringUseCase = stopPingMonitoringUseCase;
        this.getPingTargetsUseCase = getPingTargetsUseCase;
        this.processPingResultUseCase = processPingResultUseCase;
        this.cleanupDeviceDataUseCase = cleanupDeviceDataUseCase;
        this.updatePingTargetIpAddressUseCase = updatePingTargetIpAddressUseCase;
        this.updatePingTargetAddressUseCase = updatePingTargetAddressUseCase;
    }
    
    public PingTargetDomain createPingTarget(UUID deviceId, String ipAddress, Integer pingIntervalSeconds) {
        var command = new CreatePingTargetUseCase.CreatePingTargetCommand(deviceId, ipAddress, pingIntervalSeconds);
        return createPingTargetUseCase.execute(command);
    }
    
    public PingTargetDomain createPingTarget(UUID deviceId, String ipAddress, String hostname, Integer pingIntervalSeconds) {
        var command = new CreatePingTargetUseCase.CreatePingTargetCommand(deviceId, ipAddress, hostname, pingIntervalSeconds);
        return createPingTargetUseCase.execute(command);
    }
    
    public PingTargetDomain startPingMonitoring(UUID deviceId) {
        var command = new StartPingMonitoringUseCase.StartPingMonitoringCommand(deviceId);
        return startPingMonitoringUseCase.execute(command);
    }
    
    public PingTargetDomain stopPingMonitoring(UUID deviceId) {
        var command = new StopPingMonitoringUseCase.StopPingMonitoringCommand(deviceId);
        return stopPingMonitoringUseCase.execute(command);
    }
    
    @Transactional(readOnly = true)
    public List<PingTargetDomain> getAllPingTargets() {
        return getPingTargetsUseCase.getAllPingTargets();
    }
    
    @Transactional(readOnly = true)
    public List<PingTargetDomain> getAllActivePingTargets() {
        return getPingTargetsUseCase.getAllActivePingTargets();
    }
    
    @Transactional(readOnly = true)
    public Optional<PingTargetDomain> getPingTarget(UUID deviceId) {
        return getPingTargetsUseCase.getPingTarget(deviceId);
    }
    
    @Transactional(readOnly = true)
    public List<PingTargetDomain> getPingTargetsByDeviceIds(List<UUID> deviceIds) {
        return getPingTargetsUseCase.getPingTargetsByDeviceIds(deviceIds);
    }
    
    public void processPingResult(UUID deviceId, String ipAddress, boolean success, 
                                 Long responseTime, Instant timestamp) {
        var command = new ProcessPingResultUseCase.ProcessPingResultCommand(
            deviceId, ipAddress, success, responseTime, timestamp);
        processPingResultUseCase.execute(command);
    }
    
    public void cleanupDeviceData(UUID deviceId) {
        var command = new CleanupDeviceDataUseCase.CleanupDeviceDataCommand(deviceId);
        cleanupDeviceDataUseCase.execute(command);
    }
    
    public PingTargetDomain updatePingTargetIpAddress(UUID deviceId, String ipAddress) {
        var command = new UpdatePingTargetIpAddressUseCase.UpdatePingTargetIpAddressCommand(deviceId, ipAddress);
        return updatePingTargetIpAddressUseCase.execute(command);
    }
    
    public PingTargetDomain updatePingTargetAddress(UUID deviceId, String ipAddress, String hostname) {
        var command = new UpdatePingTargetAddressUseCase.UpdatePingTargetAddressCommand(deviceId, ipAddress, hostname);
        return updatePingTargetAddressUseCase.execute(command);
    }
}