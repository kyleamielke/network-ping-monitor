package io.thatworked.support.gateway.dataloader;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.AlertServiceClient;
import io.thatworked.support.gateway.client.DeviceServiceClient;
import io.thatworked.support.gateway.client.PingServiceClient;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.monitoring.DeviceMonitoringDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DeviceMonitoringBatchLoader {
    
    private final StructuredLogger logger;
    private final DeviceServiceClient deviceServiceClient;
    private final PingServiceClient pingServiceClient;
    private final AlertServiceClient alertServiceClient;
    
    public DeviceMonitoringBatchLoader(StructuredLoggerFactory loggerFactory,
                                     DeviceServiceClient deviceServiceClient,
                                     PingServiceClient pingServiceClient,
                                     AlertServiceClient alertServiceClient) {
        this.logger = loggerFactory.getLogger(DeviceMonitoringBatchLoader.class);
        this.deviceServiceClient = deviceServiceClient;
        this.pingServiceClient = pingServiceClient;
        this.alertServiceClient = alertServiceClient;
    }
    
    public Mono<Map<UUID, DeviceMonitoringDTO>> load(List<UUID> deviceIds) {
        return Mono.fromCallable(() -> {
            long startTime = System.currentTimeMillis();
            logger.with("loader", "DeviceMonitoringBatchLoader")
                  .with("operation", "batchLoad")
                  .with("batchSize", deviceIds.size())
                  .debug("Starting DataLoader batch operation");
            
            Map<UUID, DeviceMonitoringDTO> result = new HashMap<>();
            
            try {
                // Batch load devices using batch endpoint
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "deviceService")
                      .with("deviceCount", deviceIds.size())
                      .debug("Calling device service batch endpoint");
                
                List<DeviceDTO> devices = deviceServiceClient.getDevicesBatch(deviceIds);
                Map<UUID, DeviceDTO> deviceMap = devices.stream()
                    .collect(Collectors.toMap(DeviceDTO::getId, device -> device));
                
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "deviceService")
                      .with("devicesReturned", devices.size())
                      .debug("Device service batch call complete");
                
                // Batch load ping targets using batch endpoint
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "pingService")
                      .with("deviceCount", deviceIds.size())
                      .debug("Calling ping service batch endpoint");
                
                List<PingTargetDTO> pingTargets = pingServiceClient.getPingTargetsBatch(deviceIds);
                Map<UUID, PingTargetDTO> pingTargetMap = pingTargets.stream()
                    .collect(Collectors.toMap(PingTargetDTO::getDeviceId, pt -> pt));
                
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "pingService")
                      .with("pingTargetsReturned", pingTargets.size())
                      .debug("Ping service batch call complete");
                
                // Batch load alerts using batch endpoint
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "alertService")
                      .with("deviceCount", deviceIds.size())
                      .debug("Calling alert service batch endpoint");
                
                List<AlertDTO> alerts = alertServiceClient.getAlertsBatch(deviceIds);
                Map<UUID, List<AlertDTO>> alertsByDevice = alerts.stream()
                    .collect(Collectors.groupingBy(AlertDTO::getDeviceId));
                
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "alertService")
                      .with("alertsReturned", alerts.size())
                      .debug("Alert service batch call complete");
                
                // Get device status from ping results
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "pingResults")
                      .with("deviceCount", deviceIds.size())
                      .debug("Getting device statuses from ping results");
                
                Map<UUID, DeviceStatusDTO> statusMap = new HashMap<>();
                for (UUID deviceId : deviceIds) {
                    try {
                        List<PingResultDTO> results = pingServiceClient.getPingResults(deviceId, 1);
                        if (!results.isEmpty()) {
                            PingResultDTO latestResult = results.get(0);
                            DeviceDTO device = deviceMap.get(deviceId);
                            if (device != null) {
                                DeviceStatusDTO status = DeviceStatusDTO.builder()
                                    .deviceId(deviceId)
                                    .deviceName(device.getName())
                                    .ipAddress(device.getIpAddress())
                                    .online(latestResult.isSuccess())
                                    .status(latestResult.isSuccess() ? "ONLINE" : "OFFLINE")
                                    .responseTime(latestResult.getResponseTimeMs() != null ? latestResult.getResponseTimeMs().doubleValue() : null)
                                    .lastStatusChange(latestResult.getTimestamp())
                                    .build();
                                statusMap.put(deviceId, status);
                            }
                        }
                    } catch (Exception e) {
                        logger.with("deviceId", deviceId)
                              .with("error", e.getMessage())
                              .warn("Failed to get ping results for device status");
                    }
                }
                
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("phase", "pingResults")
                      .with("statusesReturned", statusMap.size())
                      .debug("Device status retrieval complete");
                
                // Build monitoring DTOs
                for (UUID deviceId : deviceIds) {
                    DeviceDTO device = deviceMap.get(deviceId);
                    if (device != null) {
                        DeviceMonitoringDTO.DeviceMonitoringDTOBuilder builder = DeviceMonitoringDTO.builder()
                            .device(device)
                            .pingTarget(pingTargetMap.get(deviceId))
                            .recentAlerts(alertsByDevice.getOrDefault(deviceId, new ArrayList<>()));
                        
                        // Add current status from dashboard cache service
                        DeviceStatusDTO currentStatus = statusMap.get(deviceId);
                        if (currentStatus != null) {
                            builder.currentStatus(currentStatus);
                        }
                        
                        result.put(deviceId, builder.build());
                    }
                }
                
                long duration = System.currentTimeMillis() - startTime;
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("deviceCount", deviceIds.size())
                      .with("resultCount", result.size())
                      .with("durationMs", duration)
                      .info("Device monitoring batch load complete");
                
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                logger.with("loader", "DeviceMonitoringBatchLoader")
                      .with("operation", "batchLoad")
                      .with("errorType", e.getClass().getSimpleName())
                      .with("errorMessage", e.getMessage())
                      .with("durationMs", duration)
                      .error("Error batch loading device monitoring data", e);
                // Return empty results for failed devices
                for (UUID deviceId : deviceIds) {
                    if (!result.containsKey(deviceId)) {
                        result.put(deviceId, DeviceMonitoringDTO.builder().build());
                    }
                }
            }
            
            return result;
        });
    }
}