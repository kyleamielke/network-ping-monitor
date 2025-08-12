package io.thatworked.support.gateway.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.alert.CreateAlertRequest;
import io.thatworked.support.gateway.dto.common.PageResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AlertServiceFallbackFactory implements FallbackFactory<AlertServiceClient> {
    
    private final StructuredLogger logger;
    
    public AlertServiceFallbackFactory(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(AlertServiceFallbackFactory.class);
    }
    
    @Override
    public AlertServiceClient create(Throwable cause) {
        return new AlertServiceClient() {
            
            @Override
            public PageResponse<AlertDTO> getAlerts(int page, int size) {
                logger.with("operation", "getAlerts")
                      .error("Alert service unavailable", cause);
                return new PageResponse<>(new ArrayList<>(), page, size, 0, 0);
            }
            
            @Override
            public AlertDTO getAlert(UUID id) {
                // Check if it's a 404 (not found) vs actual service failure
                if (cause instanceof feign.FeignException.NotFound) {
                    logger.with("operation", "getAlert")
                          .with("alertId", id)
                          .debug("Alert not found");
                    return null; // Return null for not found
                }
                
                logger.with("operation", "getAlert")
                      .with("alertId", id)
                      .error("Alert service unavailable", cause);
                throw new RuntimeException("Alert service error: " + cause.getMessage());
            }
            
            @Override
            public List<AlertDTO> getDeviceAlerts(UUID deviceId) {
                logger.with("operation", "getDeviceAlerts")
                      .with("deviceId", deviceId)
                      .error("Alert service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public List<AlertDTO> getAlertsBatch(List<UUID> deviceIds) {
                logger.with("operation", "getAlertsBatch")
                      .with("deviceCount", deviceIds.size())
                      .error("Alert service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public AlertDTO createAlert(CreateAlertRequest request) {
                logger.with("operation", "createAlert")
                      .error("Alert service unavailable", cause);
                throw new RuntimeException("Alert service unavailable: " + cause.getMessage());
            }
            
            @Override
            public AlertDTO acknowledgeAlert(UUID id) {
                logger.with("operation", "acknowledgeAlert")
                      .with("alertId", id)
                      .error("Alert service unavailable", cause);
                throw new RuntimeException("Alert service unavailable: " + cause.getMessage());
            }
            
            @Override
            public AlertDTO resolveAlert(UUID id) {
                logger.with("operation", "resolveAlert")
                      .with("alertId", id)
                      .error("Alert service unavailable", cause);
                throw new RuntimeException("Alert service unavailable: " + cause.getMessage());
            }
            
            @Override
            public void deleteAlert(UUID id) {
                logger.with("operation", "deleteAlert")
                      .with("alertId", id)
                      .error("Alert service unavailable", cause);
                throw new RuntimeException("Alert service unavailable: " + cause.getMessage());
            }
        };
    }
}