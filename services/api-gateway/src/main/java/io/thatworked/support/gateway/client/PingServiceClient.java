package io.thatworked.support.gateway.client;

import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingStatisticsDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
    name = "ping-service",
    url = "${services.ping.url:http://ping-service:8082}",
    fallbackFactory = PingServiceFallbackFactory.class
)
public interface PingServiceClient {
    
    @GetMapping("/api/v1/ping/targets")
    List<PingTargetDTO> getAllPingTargets();
    
    @GetMapping("/api/v1/ping/targets/active")
    List<PingTargetDTO> getActivePingTargets();
    
    @GetMapping("/api/v1/ping/targets/{id}")
    PingTargetDTO getPingTarget(@PathVariable("id") UUID id);
    
    @PostMapping("/api/v1/ping/targets")
    PingTargetDTO createPingTarget(@RequestBody PingTargetDTO target);
    
    @PostMapping("/api/v1/ping/targets/{id}/start")
    PingTargetDTO startMonitoring(@PathVariable("id") UUID id);
    
    @PostMapping("/api/v1/ping/targets/{id}/stop")
    PingTargetDTO stopMonitoring(@PathVariable("id") UUID id);
    
    @DeleteMapping("/api/v1/ping/targets/{id}")
    void deletePingTarget(@PathVariable("id") UUID id);
    
    @GetMapping("/api/v1/ping/results/{deviceId}")
    List<PingResultDTO> getPingResults(@PathVariable("deviceId") UUID deviceId,
                                       @RequestParam(defaultValue = "100") int limit);

    @GetMapping("/api/v1/ping/results/{deviceId}/since")
    List<PingResultDTO> getPingResultsSince(@PathVariable("deviceId") UUID deviceId,
                                           @RequestParam int minutes);
    
    @GetMapping("/api/v1/ping/statistics/{deviceId}")
    PingStatisticsDTO getPingStatistics(@PathVariable("deviceId") UUID deviceId,
                                       @RequestParam(defaultValue = "60") int minutes);
    
    @PostMapping("/api/v1/ping/targets/batch")
    List<PingTargetDTO> getPingTargetsBatch(@RequestBody List<UUID> deviceIds);
    
    // Note: These endpoints have issues in the service
    // @GetMapping("/api/v1/ping/targets/device/{deviceId}")
    // @GetMapping("/api/v1/ping/results/latest")
}