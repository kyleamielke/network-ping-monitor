package io.thatworked.support.gateway.dataloader;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.PingServiceClient;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PingTargetBatchLoader {
    
    private final StructuredLogger logger;
    private final PingServiceClient pingServiceClient;
    
    public PingTargetBatchLoader(StructuredLoggerFactory loggerFactory,
                               PingServiceClient pingServiceClient) {
        this.logger = loggerFactory.getLogger(PingTargetBatchLoader.class);
        this.pingServiceClient = pingServiceClient;
    }
    
    public Mono<Map<UUID, PingTargetDTO>> load(List<UUID> deviceIds) {
        return Mono.fromCallable(() -> {
            logger.with("operation", "load")
                  .with("deviceIds", deviceIds.size())
                  .debug("Batch loading ping target data");
            
            Map<UUID, PingTargetDTO> result = new HashMap<>();
            
            try {
                // Load all active ping targets and filter by device IDs
                List<PingTargetDTO> allPingTargets = pingServiceClient.getActivePingTargets();
                Map<UUID, PingTargetDTO> pingTargetMap = allPingTargets.stream()
                    .filter(pt -> deviceIds.contains(pt.getDeviceId()))
                    .collect(Collectors.toMap(PingTargetDTO::getDeviceId, pt -> pt));
                
                // Ensure all requested device IDs have an entry (even if null)
                for (UUID deviceId : deviceIds) {
                    result.put(deviceId, pingTargetMap.get(deviceId));
                }
                
                logger.with("operation", "load")
                      .with("deviceCount", deviceIds.size())
                      .with("pingTargetsFound", pingTargetMap.size())
                      .debug("Ping target batch load complete");
                
            } catch (Exception e) {
                logger.with("operation", "load")
                      .with("error", e.getMessage())
                      .error("Error batch loading ping target data", e);
                
                // Return null entries for all devices on error
                for (UUID deviceId : deviceIds) {
                    result.put(deviceId, null);
                }
            }
            
            return result;
        });
    }
}