package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.PingResultDTO;
import io.thatworked.support.report.infrastructure.dto.PingTargetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "ping-service", contextId = "pingTargetClient")
public interface PingTargetClient {
    
    @GetMapping("/api/v1/ping/targets")
    List<PingTargetDTO> getAllPingTargets();
    
    @GetMapping("/api/v1/ping/targets/active")
    List<PingTargetDTO> getActivePingTargets();
    
    @GetMapping("/api/v1/ping/results/{deviceId}")
    List<PingResultDTO> getPingResults(@PathVariable("deviceId") UUID deviceId, @RequestParam("limit") int limit);
}