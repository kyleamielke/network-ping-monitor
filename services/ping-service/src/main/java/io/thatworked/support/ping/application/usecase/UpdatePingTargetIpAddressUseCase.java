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
 * Use case for updating a ping target's IP address when the device IP changes.
 */
@Service
@Transactional
public class UpdatePingTargetIpAddressUseCase {
    
    private final PingTargetRepository pingTargetRepository;
    private final EventPublisher eventPublisher;
    private final StructuredLogger logger;
    
    public UpdatePingTargetIpAddressUseCase(PingTargetRepository pingTargetRepository,
                                           EventPublisher eventPublisher,
                                           StructuredLoggerFactory loggerFactory) {
        this.pingTargetRepository = pingTargetRepository;
        this.eventPublisher = eventPublisher;
        this.logger = loggerFactory.getLogger(UpdatePingTargetIpAddressUseCase.class);
    }
    
    public PingTargetDomain execute(UpdatePingTargetIpAddressCommand command) {
        UUID deviceId = command.deviceId();
        String newIpAddress = command.newIpAddress();
        
        logger.with("deviceId", deviceId)
              .with("newIpAddress", newIpAddress)
              .debug("Updating ping target IP address");
        
        // Find existing ping target
        PingTargetDomain pingTarget = pingTargetRepository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Ping target not found for device: " + deviceId));
        
        // Update IP address
        PingTargetDomain updatedPingTarget = pingTarget.withIpAddress(newIpAddress);
        
        // Save updated ping target
        PingTargetDomain saved = pingTargetRepository.save(updatedPingTarget);
        
        // Publish event if the target is monitored
        if (saved.isMonitored()) {
            eventPublisher.publishPingTargetIpUpdated(saved, pingTarget.getIpAddress());
        }
        
        logger.with("deviceId", deviceId)
              .with("oldIpAddress", pingTarget.getIpAddress())
              .with("newIpAddress", newIpAddress)
              .info("Updated ping target IP address");
        
        return saved;
    }
    
    public record UpdatePingTargetIpAddressCommand(UUID deviceId, String newIpAddress) {
        public UpdatePingTargetIpAddressCommand {
            if (deviceId == null) throw new IllegalArgumentException("Device ID is required");
            if (newIpAddress == null || newIpAddress.isBlank()) {
                throw new IllegalArgumentException("IP address is required");
            }
        }
    }
}