package io.thatworked.support.ping.application.service;

import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.infrastructure.event.PingTargetStartedEvent;
import io.thatworked.support.ping.infrastructure.event.PingTargetStoppedEvent;
import io.thatworked.support.common.exception.EntityNotFoundException;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class PingTargetService {
    private final StructuredLogger logger;
    private final PingTargetRepository pingTargetRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AlertStateService alertStateService;

    public PingTargetService(StructuredLoggerFactory structuredLoggerFactory,
                           PingTargetRepository pingTargetRepository,
                           ApplicationEventPublisher eventPublisher,
                           AlertStateService alertStateService) {
        this.logger = structuredLoggerFactory.getLogger(PingTargetService.class);
        this.pingTargetRepository = pingTargetRepository;
        this.eventPublisher = eventPublisher;
        this.alertStateService = alertStateService;
    }

    @Transactional(readOnly = true)
    public List<PingTarget> findAllPingTargets() {
        return pingTargetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<PingTarget> findAllActivePingTargets() {
        return pingTargetRepository.findAllActiveTargets();
    }

    @Transactional(readOnly = true)
    public PingTarget getPingTarget(UUID id) {
        return pingTargetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PingTarget", id.toString()));
    }

    @Transactional
    public PingTarget createPingTarget(UUID deviceId, String ipAddress, Integer pingIntervalSeconds) {
        // Check if ping target already exists
        if (pingTargetRepository.existsById(deviceId)) {
            PingTarget existingTarget = pingTargetRepository.findById(deviceId).orElseThrow();
            // Update IP address and interval if they've changed
            if (!ipAddress.equals(existingTarget.getIpAddress()) ||
                !Objects.equals(pingIntervalSeconds, existingTarget.getPingIntervalSeconds())) {
                existingTarget.setIpAddress(ipAddress);
                existingTarget.setPingIntervalSeconds(pingIntervalSeconds);
                existingTarget.setUpdatedAt(Instant.now());
                return pingTargetRepository.save(existingTarget);
            }
            return existingTarget;
        }

        PingTarget pingTarget = PingTarget.builder()
                .deviceId(deviceId)
                .ipAddress(ipAddress)
                .pingIntervalSeconds(pingIntervalSeconds)
                .isMonitored(false)
                .build();

        return pingTargetRepository.save(pingTarget);
    }

    @Transactional
    public PingTarget updatePingInterval(UUID id, Integer pingIntervalSeconds) {
        PingTarget pingTarget = getPingTarget(id);
        pingTarget.setPingIntervalSeconds(pingIntervalSeconds);
        pingTarget.setUpdatedAt(Instant.now());

        PingTarget saved = pingTargetRepository.save(pingTarget);

        // If the target is already being monitored, we need to remove and re-add it
        // to update the interval in the active tasks
        if (saved.isMonitored()) {
            eventPublisher.publishEvent(new PingTargetStoppedEvent(id));
            eventPublisher.publishEvent(new PingTargetStartedEvent(saved));
        }

        return saved;
    }

    @Transactional
    public PingTarget startPinging(UUID id) {
        PingTarget pingTarget = getPingTarget(id);
        pingTarget.setMonitored(true);
        pingTarget.setUpdatedAt(Instant.now());

        PingTarget saved = pingTargetRepository.save(pingTarget);

        // Publish internal event to notify queue manager
        eventPublisher.publishEvent(new PingTargetStartedEvent(saved));
        
        // Publish Kafka event for monitoring state change
        alertStateService.publishMonitoringStartedEvent(saved.getDeviceId());

        return saved;
    }

    @Transactional
    public PingTarget stopPinging(UUID id) {
        PingTarget pingTarget = getPingTarget(id);
        pingTarget.setMonitored(false);
        pingTarget.setUpdatedAt(Instant.now());

        PingTarget saved = pingTargetRepository.save(pingTarget);

        // Publish internal event to notify queue manager
        eventPublisher.publishEvent(new PingTargetStoppedEvent(id));
        
        // Publish Kafka event for monitoring state change
        alertStateService.publishMonitoringStoppedEvent(saved.getDeviceId());

        return saved;
    }

    @Transactional
    public void deletePingTarget(UUID id) {
        if (!pingTargetRepository.existsById(id)) {
            throw new EntityNotFoundException("PingTarget", id.toString());
        }

        // Stop pinging first
        stopPinging(id);

        pingTargetRepository.deleteById(id);
    }
    
    @Transactional
    public void deletePingTargetByDeviceId(UUID deviceId) {
        logger.with("deviceId", deviceId).info("Deleting ping target for device");
        
        // Check if ping target exists
        Optional<PingTarget> target = pingTargetRepository.findById(deviceId);
        
        if (target.isPresent()) {
            // Stop monitoring if active
            if (target.get().isMonitored()) {
                try {
                    stopPinging(deviceId);
                } catch (Exception e) {
                    logger.with("deviceId", deviceId).with("error", e.getMessage()).warn("Failed to stop pinging for device before deletion");
                }
            }
            
            // Delete the ping target (cascade will handle ping_results)
            pingTargetRepository.deleteById(deviceId);
            logger.with("deviceId", deviceId).info("Successfully deleted ping target for device");
            
            // Note: ping_results table has ON DELETE CASCADE, so results are automatically deleted
        } else {
            logger.with("deviceId", deviceId).debug("No ping target found for device");
        }
    }
}