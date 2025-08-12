package io.thatworked.support.gateway.dataloader;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.AlertServiceClient;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.alert.AlertListDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AlertsBatchLoader {
    
    private final StructuredLogger logger;
    private final AlertServiceClient alertServiceClient;
    
    public AlertsBatchLoader(StructuredLoggerFactory loggerFactory,
                           AlertServiceClient alertServiceClient) {
        this.logger = loggerFactory.getLogger(AlertsBatchLoader.class);
        this.alertServiceClient = alertServiceClient;
    }
    
    public Mono<Map<UUID, AlertListDTO>> load(List<UUID> deviceIds) {
        return Mono.fromCallable(() -> {
            logger.with("operation", "load")
                  .with("deviceIds", deviceIds.size())
                  .debug("Batch loading alerts data");
            
            Map<UUID, AlertListDTO> result = new HashMap<>();
            
            try {
                // Batch load alerts using batch endpoint
                List<AlertDTO> allAlerts = alertServiceClient.getAlertsBatch(deviceIds);
                
                // Group alerts by device ID
                Map<UUID, List<AlertDTO>> alertsByDevice = allAlerts.stream()
                    .collect(Collectors.groupingBy(AlertDTO::getDeviceId));
                
                // Build result map with all requested device IDs
                for (UUID deviceId : deviceIds) {
                    List<AlertDTO> deviceAlerts = alertsByDevice.getOrDefault(deviceId, Collections.emptyList());
                    AlertListDTO alertList = AlertListDTO.builder()
                        .alerts(deviceAlerts)
                        .totalCount(deviceAlerts.size())
                        .build();
                    result.put(deviceId, alertList);
                }
                
                logger.with("operation", "load")
                      .with("deviceCount", deviceIds.size())
                      .with("resultCount", result.size())
                      .debug("Alerts batch load complete");
                
            } catch (Exception e) {
                logger.with("operation", "load")
                      .with("error", e.getMessage())
                      .error("Error batch loading alerts data", e);
                
                // Return empty results for all devices on error
                for (UUID deviceId : deviceIds) {
                    if (!result.containsKey(deviceId)) {
                        result.put(deviceId, AlertListDTO.builder()
                            .alerts(Collections.emptyList())
                            .totalCount(0)
                            .build());
                    }
                }
            }
            
            return result;
        });
    }
}