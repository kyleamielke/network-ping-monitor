package io.thatworked.support.ping.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.ping.application.service.MonitoredDeviceService;
import io.thatworked.support.ping.infrastructure.config.KafkaConfig;
import io.thatworked.support.ping.domain.AlertState;
import io.thatworked.support.ping.domain.MonitoredDevice;
import io.thatworked.support.ping.api.dto.MonitoringEvent;
import io.thatworked.support.ping.api.dto.PingResultDTO;
import io.thatworked.support.ping.infrastructure.event.PingResultEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceDownEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceRecoveredEvent;
import io.thatworked.support.ping.infrastructure.repository.jpa.AlertStateRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AlertStateService {

    private final StructuredLogger logger;
    private final AlertStateRepository alertStateRepository;
    private final PingTargetRepository pingTargetRepository;
    private final MonitoredDeviceService monitoredDeviceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    public AlertStateService(StructuredLoggerFactory structuredLoggerFactory,
                           AlertStateRepository alertStateRepository,
                           PingTargetRepository pingTargetRepository,
                           MonitoredDeviceService monitoredDeviceService,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.logger = structuredLoggerFactory.getLogger(AlertStateService.class);
        this.alertStateRepository = alertStateRepository;
        this.pingTargetRepository = pingTargetRepository;
        this.monitoredDeviceService = monitoredDeviceService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Value("${ping.alerting.failure-threshold:3}")
    private int failureThreshold;

    @Value("${ping.alerting.recovery-threshold:2}")
    private int recoveryThreshold;

    @Value("${ping.alerting.enabled:true}")
    private boolean alertingEnabled;

    @EventListener
    @Transactional
    public void handlePingResult(PingResultEvent event) {
        UUID deviceId = event.getPingResult().getDeviceId();
        boolean success = event.getPingResult().getStatus().isSuccess();
        Double rtt = event.getPingResult().getRoundTripTime();

        // Always publish standardized ping result event for real-time monitoring
        try {
            // Get device info for context
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            PingResultDTO dto = PingResultDTO.fromDomain(event.getPingResult());
            io.thatworked.support.ping.api.dto.PingResultEvent pingEvent = 
                io.thatworked.support.ping.api.dto.PingResultEvent.fromPingResult(dto, deviceName, ipAddress);
            
            kafkaTemplate.send(KafkaConfig.PING_RESULTS_TOPIC, deviceId.toString(), pingEvent);
            logger.with("deviceId", deviceId).with("status", event.getPingResult().getStatus()).debug("Published ping result event to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId).with("error", e.getMessage()).error("Failed to publish ping result event to Kafka", e);
        }

        // Only process alerting if enabled
        if (!alertingEnabled) {
            return;
        }

        AlertState alertState = alertStateRepository.findById(deviceId)
                .orElseGet(() -> {
                    AlertState newState = new AlertState();
                    newState.setDeviceId(deviceId);
                    return newState;
                });

        if (success) {
            handleSuccessfulPing(alertState, rtt != null ? rtt : 0.0);
        } else {
            handleFailedPing(alertState);
        }

        alertStateRepository.save(alertState);
    }

    private void handleSuccessfulPing(AlertState alertState, double responseTime) {
        alertState.recordSuccess();

        // Check if device has recovered
        if (alertState.isAlerting() && alertState.getConsecutiveSuccesses() >= recoveryThreshold) {
            alertState.markRecovered();
            publishRecoveryMonitoringEvent(alertState.getDeviceId(), alertState.getLastFailureTime(), responseTime, alertState.getConsecutiveSuccesses());
            // Keep legacy events for backward compatibility during transition
            publishRecoveryEvent(alertState.getDeviceId(), alertState.getLastFailureTime(), responseTime, alertState.getConsecutiveSuccesses());
            logger.with("deviceId", alertState.getDeviceId()).with("consecutiveSuccesses", alertState.getConsecutiveSuccesses()).info("Device has recovered after consecutive successes");
        }
        // Publish baseline event for healthy devices to populate cache
        else if (!alertState.isAlerting() && alertState.getConsecutiveSuccesses() == 1) {
            publishHealthyDeviceEvent(alertState.getDeviceId(), responseTime);
            logger.with("deviceId", alertState.getDeviceId()).debug("Published baseline healthy event for device");
        }
    }

    private void handleFailedPing(AlertState alertState) {
        alertState.recordFailure();

        // Check if we need to send an alert
        if (!alertState.isAlerting() && alertState.getConsecutiveFailures() >= failureThreshold) {
            alertState.markAlerting();
            publishDownMonitoringEvent(alertState.getDeviceId(), alertState.getLastSuccessTime(), alertState.getConsecutiveFailures());
            // Keep legacy events for backward compatibility during transition
            publishDownEvent(alertState.getDeviceId(), alertState.getLastSuccessTime(), alertState.getConsecutiveFailures());
            logger.with("deviceId", alertState.getDeviceId()).with("consecutiveFailures", alertState.getConsecutiveFailures()).warn("Device is down after consecutive failures");
        }
    }

    private void publishDownEvent(UUID deviceId, Instant lastSuccessTime, int consecutiveFailures) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            DeviceDownEvent event = new DeviceDownEvent(
                deviceId,
                deviceName,
                ipAddress,
                consecutiveFailures,
                lastSuccessTime,
                "Device not responding to ping"
            );
            
            // Send to specific topic and generic alerts topic
            kafkaTemplate.send(KafkaConfig.DEVICE_DOWN_TOPIC, deviceId.toString(), event);
            kafkaTemplate.send(KafkaConfig.DEVICE_ALERTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published DeviceDownEvent for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId)
                  .with("error", e.getMessage())
                  .error("Failed to publish down event for device", e);
        }
    }

    private void publishRecoveryEvent(UUID deviceId, Instant downSince, double responseTime, int consecutiveSuccesses) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            DeviceRecoveredEvent event = new DeviceRecoveredEvent(
                deviceId,
                deviceName,
                ipAddress,
                downSince,
                responseTime,
                consecutiveSuccesses
            );
            
            // Send to specific topic and generic alerts topic
            kafkaTemplate.send(KafkaConfig.DEVICE_RECOVERED_TOPIC, deviceId.toString(), event);
            kafkaTemplate.send(KafkaConfig.DEVICE_ALERTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published DeviceRecoveredEvent for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId)
                  .with("error", e.getMessage())
                  .error("Failed to publish recovery event for device", e);
        }
    }

    private void publishHealthyDeviceEvent(UUID deviceId, double responseTime) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            // For healthy baseline devices, use current time as both downSince and now
            // so the duration calculates to zero, avoiding null issues
            Instant now = Instant.now();
            DeviceRecoveredEvent event = new DeviceRecoveredEvent(
                deviceId,
                deviceName,
                ipAddress,
                now, // Use current time instead of null for baseline healthy devices
                responseTime,
                1 // consecutive successes
            );
            
            // Send to alerts topic to populate device status cache
            kafkaTemplate.send(KafkaConfig.DEVICE_ALERTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).debug("Published baseline healthy event for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId)
                  .with("error", e.getMessage())
                  .error("Failed to publish healthy device event for device", e);
        }
    }

    @Scheduled(fixedDelayString = "${ping.alerting.check-interval-seconds:60}000")
    @Transactional
    public void checkStaleAlerts() {
        if (!alertingEnabled) {
            return;
        }

        // Check for devices that haven't been pinged recently
        Instant staleThreshold = Instant.now().minusSeconds(300); // 5 minutes
        
        alertStateRepository.findByIsAlertingTrue().forEach(alertState -> {
            if (alertState.getUpdatedAt().isBefore(staleThreshold)) {
                logger.with("deviceId", alertState.getDeviceId()).with("lastUpdated", alertState.getUpdatedAt()).warn("Alert state for device is stale");
            }
        });
    }

    @Scheduled(fixedDelayString = "30000", initialDelay = 15000) // Every 30 seconds, 15 second delay
    @Transactional
    public void publishHealthyDeviceSync() {
        if (!alertingEnabled) {
            return;
        }

        logger.with("method", "publishHealthyDeviceSync")
              .info("Starting periodic healthy device sync for explicitly monitored devices");
        
        // Get only actively monitored devices (those with active PingTargets)
        var activeTargets = pingTargetRepository.findAllActiveTargets();
        final int[] syncCount = {0}; // Use array for lambda mutability
        
        for (var target : activeTargets) {
            UUID deviceId = target.getDeviceId();
            
            // Only sync if device has an AlertState and is healthy
            alertStateRepository.findById(deviceId).ifPresent(alertState -> {
                if (!alertState.isAlerting() && alertState.getConsecutiveSuccesses() > 0) {
                    double responseTime = 2.0; // Default response time for sync
                    publishHealthyDeviceEvent(deviceId, responseTime);
                    syncCount[0]++;
                    logger.with("deviceId", deviceId)
                          .debug("Published sync event for actively monitored healthy device");
                }
            });
        }
        
        logger.with("method", "publishHealthyDeviceSync")
              .with("activeTargets", activeTargets.size())
              .with("syncedDevices", syncCount[0])
              .info("Completed healthy device sync - only explicitly monitored devices included");
    }

    private void publishDownMonitoringEvent(UUID deviceId, Instant lastSuccessTime, int consecutiveFailures) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            MonitoringEvent event = MonitoringEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .eventType(MonitoringEvent.EventType.DEVICE_DOWN)
                .source(MonitoringEvent.Source.PING_SERVICE)
                .message("Device not responding to ping")
                .timestamp(Instant.now())
                .consecutiveFailures(consecutiveFailures)
                .lastSuccessTime(lastSuccessTime)
                .build();
            
            kafkaTemplate.send(KafkaConfig.PING_MONITORING_EVENTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published MonitoringEvent (DEVICE_DOWN) for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId).with("error", e.getMessage()).error("Failed to publish monitoring down event for device", e);
        }
    }

    private void publishRecoveryMonitoringEvent(UUID deviceId, Instant lastFailureTime, double responseTime, int consecutiveSuccesses) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            MonitoringEvent event = MonitoringEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .eventType(MonitoringEvent.EventType.DEVICE_RECOVERED)
                .source(MonitoringEvent.Source.PING_SERVICE)
                .message("Device responding normally to ping")
                .timestamp(Instant.now())
                .consecutiveSuccesses(consecutiveSuccesses)
                .responseTimeMs(responseTime)
                .lastFailureTime(lastFailureTime)
                .build();
            
            kafkaTemplate.send(KafkaConfig.PING_MONITORING_EVENTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published MonitoringEvent (DEVICE_RECOVERED) for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId).with("error", e.getMessage()).error("Failed to publish monitoring recovery event for device", e);
        }
    }
    
    public void publishMonitoringStartedEvent(UUID deviceId) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            MonitoringEvent event = MonitoringEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .eventType(MonitoringEvent.EventType.MONITORING_STARTED)
                .source(MonitoringEvent.Source.PING_SERVICE)
                .message("Monitoring started for device")
                .timestamp(Instant.now())
                .build();
            
            kafkaTemplate.send(KafkaConfig.PING_MONITORING_EVENTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published MonitoringEvent (MONITORING_STARTED) for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId).with("error", e.getMessage()).error("Failed to publish monitoring started event for device", e);
        }
    }
    
    public void publishMonitoringStoppedEvent(UUID deviceId) {
        try {
            MonitoredDevice device = monitoredDeviceService.findById(deviceId).orElse(null);
            String deviceName = device != null ? device.getDeviceName() : "Unknown Device";
            String ipAddress = device != null ? device.getIpAddress() : "Unknown IP";
            
            MonitoringEvent event = MonitoringEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .ipAddress(ipAddress)
                .eventType(MonitoringEvent.EventType.MONITORING_STOPPED)
                .source(MonitoringEvent.Source.PING_SERVICE)
                .message("Monitoring stopped for device")
                .timestamp(Instant.now())
                .build();
            
            kafkaTemplate.send(KafkaConfig.PING_MONITORING_EVENTS_TOPIC, deviceId.toString(), event);
            
            logger.with("deviceId", deviceId).info("Published MonitoringEvent (MONITORING_STOPPED) for device to Kafka");
        } catch (Exception e) {
            logger.with("deviceId", deviceId).with("error", e.getMessage()).error("Failed to publish monitoring stopped event for device", e);
        }
    }
}