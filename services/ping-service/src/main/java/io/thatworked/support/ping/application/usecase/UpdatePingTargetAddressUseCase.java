package io.thatworked.support.ping.application.usecase;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingTargetRepository;
import io.thatworked.support.ping.domain.port.EventPublisher;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case for updating a ping target's IP address and/or hostname when the device changes.
 */
@Service
@Transactional
public class UpdatePingTargetAddressUseCase {
    
    private final PingTargetRepository pingTargetRepository;
    private final EventPublisher eventPublisher;
    private final StructuredLogger logger;
    
    public UpdatePingTargetAddressUseCase(PingTargetRepository pingTargetRepository,
                                         EventPublisher eventPublisher,
                                         StructuredLoggerFactory loggerFactory) {
        this.pingTargetRepository = pingTargetRepository;
        this.eventPublisher = eventPublisher;
        this.logger = loggerFactory.getLogger(UpdatePingTargetAddressUseCase.class);
    }
    
    public PingTargetDomain execute(UpdatePingTargetAddressCommand command) {
        UUID deviceId = command.deviceId();
        String newIpAddress = command.newIpAddress();
        String newHostname = command.newHostname();
        
        logger.with("deviceId", deviceId)
              .with("newIpAddress", newIpAddress)
              .with("newHostname", newHostname)
              .debug("Updating ping target address");
        
        // Find existing ping target
        PingTargetDomain pingTarget = pingTargetRepository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Ping target not found for device: " + deviceId));
        
        // Track changes
        String oldIpAddress = pingTarget.getIpAddress();
        String oldHostname = pingTarget.getHostname();
        boolean ipChanged = (newIpAddress != null && !newIpAddress.equals(oldIpAddress)) ||
                           (newIpAddress == null && oldIpAddress != null);
        boolean hostnameChanged = (newHostname != null && !newHostname.equals(oldHostname)) ||
                                 (newHostname == null && oldHostname != null);
        
        // Update addresses
        PingTargetDomain updatedPingTarget = pingTarget;
        if (newIpAddress != null || pingTarget.getIpAddress() != null) {
            updatedPingTarget = updatedPingTarget.withIpAddress(newIpAddress);
        }
        if (newHostname != null || pingTarget.getHostname() != null) {
            updatedPingTarget = updatedPingTarget.withHostname(newHostname);
        }
        
        // Save updated ping target
        PingTargetDomain saved = pingTargetRepository.save(updatedPingTarget);
        
        // Publish event if the target is monitored and addresses changed
        if (saved.isMonitored() && (ipChanged || hostnameChanged)) {
            eventPublisher.publishPingTargetAddressUpdated(saved, oldIpAddress, oldHostname);
        }
        
        logger.with("deviceId", deviceId)
              .with("oldIpAddress", oldIpAddress)
              .with("newIpAddress", newIpAddress)
              .with("oldHostname", oldHostname)
              .with("newHostname", newHostname)
              .with("ipChanged", ipChanged)
              .with("hostnameChanged", hostnameChanged)
              .info("Updated ping target address");
        
        return saved;
    }
    
    public record UpdatePingTargetAddressCommand(UUID deviceId, String newIpAddress, String newHostname) {
        public UpdatePingTargetAddressCommand {
            if (deviceId == null) throw new IllegalArgumentException("Device ID is required");
            // At least one address must be provided
            if ((newIpAddress == null || newIpAddress.isBlank()) && 
                (newHostname == null || newHostname.isBlank())) {
                throw new IllegalArgumentException("At least one address (IP or hostname) is required");
            }
        }
    }
}