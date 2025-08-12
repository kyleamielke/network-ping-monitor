package io.thatworked.support.gateway.dataloader;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.PingServiceClient;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class RecentPingsBatchLoader {
    
    private final StructuredLogger logger;
    private final PingServiceClient pingServiceClient;
    
    public RecentPingsBatchLoader(StructuredLoggerFactory loggerFactory,
                                PingServiceClient pingServiceClient) {
        this.logger = loggerFactory.getLogger(RecentPingsBatchLoader.class);
        this.pingServiceClient = pingServiceClient;
    }
    
    public Mono<Map<UUID, List<PingResultDTO>>> load(List<UUID> deviceIds) {
        return Mono.fromCallable(() -> {
            logger.with("operation", "load")
                  .with("deviceIds", deviceIds.size())
                  .debug("Batch loading recent ping data for mini indicators");
            
            Map<UUID, List<PingResultDTO>> result = new HashMap<>();
            
            try {
                // Load recent pings for each device (last 50 pings for dynamic mini indicator)
                for (UUID deviceId : deviceIds) {
                    try {
                        List<PingResultDTO> recentPings = pingServiceClient.getPingResults(deviceId, 50);
                        // Sort by timestamp descending (most recent first) for display
                        recentPings.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                        result.put(deviceId, recentPings);
                    } catch (Exception e) {
                        logger.with("operation", "load")
                              .with("deviceId", deviceId)
                              .with("error", e.getMessage())
                              .warn("Failed to load recent pings for device");
                        // Return empty list for failed devices
                        result.put(deviceId, Collections.emptyList());
                    }
                }
                
                logger.with("operation", "load")
                      .with("deviceCount", deviceIds.size())
                      .with("successfulLoads", result.entrySet().stream()
                          .mapToInt(entry -> entry.getValue().isEmpty() ? 0 : 1)
                          .sum())
                      .debug("Recent pings batch load complete");
                
            } catch (Exception e) {
                logger.with("operation", "load")
                      .with("error", e.getMessage())
                      .error("Error batch loading recent ping data", e);
                
                // Return empty lists for all devices on error
                for (UUID deviceId : deviceIds) {
                    result.put(deviceId, Collections.emptyList());
                }
            }
            
            return result;
        });
    }
}