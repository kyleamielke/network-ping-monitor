package io.thatworked.support.gateway.client;

import io.thatworked.support.gateway.dto.common.PageResponse;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "device-service",
    url = "${services.device.url:http://device-service:8081}",
    fallbackFactory = DeviceServiceFallbackFactory.class
)
public interface DeviceServiceClient {
    
    @GetMapping("/api/v1/devices")
    PageResponse<DeviceDTO> getDevices(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size);
    
    @GetMapping("/api/v1/devices/{id}")
    DeviceDTO getDevice(@PathVariable("id") UUID id);
    
    @PostMapping("/api/v1/devices")
    DeviceDTO createDevice(@RequestBody DeviceDTO device);
    
    @PutMapping("/api/v1/devices/{id}")
    DeviceDTO updateDevice(@PathVariable("id") UUID id, @RequestBody DeviceDTO device);
    
    @DeleteMapping("/api/v1/devices/{id}")
    void deleteDevice(@PathVariable("id") UUID id);
    
    @PostMapping("/api/v1/devices/search")
    PageResponse<DeviceDTO> searchDevices(@RequestBody Object searchCriteria);
    
    @GetMapping("/api/v1/devices/type/{type}")
    List<DeviceDTO> getDevicesByType(@PathVariable("type") String type);
    
    @PostMapping("/api/v1/devices/batch")
    List<DeviceDTO> getDevicesBatch(@RequestBody List<UUID> deviceIds);
    
    @DeleteMapping("/api/v1/devices/bulk")
    Map<String, Object> bulkDeleteDevices(@RequestBody List<UUID> deviceIds);
    
    @PutMapping("/api/v1/devices/bulk")
    Map<String, Object> bulkUpdateDevices(@RequestBody Map<String, Object> request);
    
    default Map<String, Object> bulkUpdateDevices(List<UUID> deviceIds, Map<String, Object> updates) {
        Map<String, Object> request = new HashMap<>();
        request.put("deviceIds", deviceIds);
        request.put("updates", updates);
        return bulkUpdateDevices(request);
    }
}