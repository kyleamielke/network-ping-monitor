package io.thatworked.support.gateway.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.common.PageResponse;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class DeviceServiceFallbackFactory implements FallbackFactory<DeviceServiceClient> {
    
    private final StructuredLogger logger;
    
    public DeviceServiceFallbackFactory(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DeviceServiceFallbackFactory.class);
    }
    
    @Override
    public DeviceServiceClient create(Throwable cause) {
        return new DeviceServiceClient() {
            
            @Override
            public PageResponse<DeviceDTO> getDevices(int page, int size) {
                logger.with("operation", "getDevices")
                      .error("Device service unavailable", cause);
                return new PageResponse<>(new ArrayList<>(), page, size, 0, 0);
            }
            
            @Override
            public DeviceDTO getDevice(UUID id) {
                // Check if it's a 404 (not found) vs actual service failure
                if (cause instanceof feign.FeignException.NotFound) {
                    logger.with("operation", "getDevice")
                          .with("deviceId", id)
                          .debug("Device not found");
                    return null; // Return null for not found
                }
                
                logger.with("operation", "getDevice")
                      .with("deviceId", id)
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service error: " + cause.getMessage());
            }
            
            @Override
            public DeviceDTO createDevice(DeviceDTO device) {
                logger.with("operation", "createDevice")
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service unavailable: " + cause.getMessage());
            }
            
            @Override
            public DeviceDTO updateDevice(UUID id, DeviceDTO device) {
                logger.with("operation", "updateDevice")
                      .with("deviceId", id)
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service unavailable: " + cause.getMessage());
            }
            
            @Override
            public void deleteDevice(UUID id) {
                logger.with("operation", "deleteDevice")
                      .with("deviceId", id)
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service unavailable: " + cause.getMessage());
            }
            
            @Override
            public PageResponse<DeviceDTO> searchDevices(Object searchCriteria) {
                logger.with("operation", "searchDevices")
                      .error("Device service unavailable", cause);
                return new PageResponse<>(new ArrayList<>(), 0, 20, 0, 0);
            }
            
            @Override
            public List<DeviceDTO> getDevicesByType(String type) {
                logger.with("operation", "getDevicesByType")
                      .with("type", type)
                      .error("Device service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public List<DeviceDTO> getDevicesBatch(List<UUID> deviceIds) {
                logger.with("operation", "getDevicesBatch")
                      .with("deviceCount", deviceIds.size())
                      .error("Device service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public Map<String, Object> bulkDeleteDevices(List<UUID> deviceIds) {
                logger.with("operation", "bulkDeleteDevices")
                      .with("deviceCount", deviceIds.size())
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service unavailable: " + cause.getMessage());
            }
            
            @Override
            public Map<String, Object> bulkUpdateDevices(Map<String, Object> request) {
                logger.with("operation", "bulkUpdateDevices")
                      .error("Device service unavailable", cause);
                throw new RuntimeException("Device service unavailable: " + cause.getMessage());
            }
        };
    }
}