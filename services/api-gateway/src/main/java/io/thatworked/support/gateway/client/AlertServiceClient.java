package io.thatworked.support.gateway.client;

import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.alert.CreateAlertRequest;
import io.thatworked.support.gateway.dto.common.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
    name = "alert-service",
    url = "${services.alert.url:http://alert-service:8084}",
    fallbackFactory = AlertServiceFallbackFactory.class
)
public interface AlertServiceClient {
    
    @GetMapping("/api/v1/alerts")
    PageResponse<AlertDTO> getAlerts(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size);
    
    @GetMapping("/api/v1/alerts/{id}")
    AlertDTO getAlert(@PathVariable("id") UUID id);
    
    @GetMapping("/api/v1/alerts/device/{deviceId}")
    List<AlertDTO> getDeviceAlerts(@PathVariable("deviceId") UUID deviceId);
    
    @PostMapping("/api/v1/alerts/batch")
    List<AlertDTO> getAlertsBatch(@RequestBody List<UUID> deviceIds);
    
    // Note: This endpoint has issues in the service
    // @GetMapping("/api/v1/alerts/unresolved")
    // List<AlertDTO> getUnresolvedAlerts();
    
    @PostMapping("/api/v1/alerts")
    AlertDTO createAlert(@RequestBody CreateAlertRequest request);
    
    @PutMapping("/api/v1/alerts/{id}/acknowledge")
    AlertDTO acknowledgeAlert(@PathVariable("id") UUID id);
    
    @PutMapping("/api/v1/alerts/{id}/resolve")
    AlertDTO resolveAlert(@PathVariable("id") UUID id);
    
    @DeleteMapping("/api/v1/alerts/{id}")
    void deleteAlert(@PathVariable("id") UUID id);
}