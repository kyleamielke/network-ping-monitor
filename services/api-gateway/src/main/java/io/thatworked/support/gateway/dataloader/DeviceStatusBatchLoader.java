package io.thatworked.support.gateway.dataloader;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.PingServiceClient;
import io.thatworked.support.gateway.client.DeviceServiceClient;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Batch loader for DeviceStatus data to avoid N+1 queries
 */
@Component
public class DeviceStatusBatchLoader {
    
    private final StructuredLogger logger;
    private final PingServiceClient pingServiceClient;
    private final DeviceServiceClient deviceServiceClient;
    
    public DeviceStatusBatchLoader(StructuredLoggerFactory loggerFactory,
                                  PingServiceClient pingServiceClient,
                                  DeviceServiceClient deviceServiceClient) {
        this.logger = loggerFactory.getLogger(DeviceStatusBatchLoader.class);
        this.pingServiceClient = pingServiceClient;
        this.deviceServiceClient = deviceServiceClient;
    }
    
    public Mono<Map<UUID, DeviceStatusDTO>> load(Set<UUID> deviceIds) {
        logger.with("operation", "deviceStatusBatchLoader")
              .with("deviceCount", deviceIds.size())
              .debug("Batch loading device statuses");
        
        return Mono.fromCallable(() -> {
            try {
                // Get device information
                List<DeviceDTO> devices = deviceServiceClient.getDevicesBatch(new ArrayList<>(deviceIds));
                Map<UUID, DeviceDTO> deviceMap = devices.stream()
                    .collect(Collectors.toMap(DeviceDTO::getId, d -> d));
                
                // Build status for each device from ping results
                Map<UUID, DeviceStatusDTO> result = new HashMap<>();
                for (UUID deviceId : deviceIds) {
                    try {
                        List<PingResultDTO> results = pingServiceClient.getPingResults(deviceId, 1);
                        DeviceDTO device = deviceMap.get(deviceId);
                        
                        if (device != null && !results.isEmpty()) {
                            PingResultDTO latestResult = results.get(0);
                            DeviceStatusDTO status = DeviceStatusDTO.builder()
                                .deviceId(deviceId)
                                .deviceName(device.getName())
                                .ipAddress(device.getIpAddress())
                                .online(latestResult.isSuccess())
                                .status(latestResult.isSuccess() ? "ONLINE" : "OFFLINE")
                                .responseTime(latestResult.getResponseTimeMs() != null ? latestResult.getResponseTimeMs().doubleValue() : null)
                                .lastStatusChange(latestResult.getTimestamp())
                                .build();
                            result.put(deviceId, status);
                        } else {
                            result.put(deviceId, null);
                        }
                    } catch (Exception e) {
                        logger.with("deviceId", deviceId)
                              .with("error", e.getMessage())
                              .warn("Failed to get status for device");
                        result.put(deviceId, null);
                    }
                }
                
                logger.with("operation", "deviceStatusBatchLoader")
                      .with("requestedDevices", deviceIds.size())
                      .with("foundStatuses", result.size())
                      .info("Batch loaded device statuses");
                
                return result;
            } catch (Exception e) {
                logger.with("operation", "deviceStatusBatchLoader")
                      .with("error", e.getMessage())
                      .error("Failed to batch load device statuses", e);
                
                // Return empty map for all devices on error
                Map<UUID, DeviceStatusDTO> errorResult = new HashMap<>();
                for (UUID deviceId : deviceIds) {
                    errorResult.put(deviceId, null);
                }
                return errorResult;
            }
        });
    }
}