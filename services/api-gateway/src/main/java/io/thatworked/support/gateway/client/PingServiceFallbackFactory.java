package io.thatworked.support.gateway.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingStatisticsDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class PingServiceFallbackFactory implements FallbackFactory<PingServiceClient> {
    
    private final StructuredLogger logger;
    
    public PingServiceFallbackFactory(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(PingServiceFallbackFactory.class);
    }
    
    @Override
    public PingServiceClient create(Throwable cause) {
        return new PingServiceClient() {
            
            @Override
            public List<PingTargetDTO> getAllPingTargets() {
                logger.with("operation", "getAllPingTargets")
                      .error("Ping service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public List<PingTargetDTO> getActivePingTargets() {
                logger.with("operation", "getActivePingTargets")
                      .error("Ping service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public PingTargetDTO getPingTarget(UUID deviceId) {
                logger.with("operation", "getPingTarget")
                      .with("deviceId", deviceId)
                      .error("Ping service unavailable", cause);
                return null;
            }
            
            @Override
            public PingTargetDTO createPingTarget(PingTargetDTO target) {
                logger.with("operation", "createPingTarget")
                      .error("Ping service unavailable", cause);
                throw new RuntimeException("Ping service unavailable: " + cause.getMessage());
            }
            
            @Override
            public PingTargetDTO startMonitoring(UUID deviceId) {
                logger.with("operation", "startMonitoring")
                      .with("deviceId", deviceId)
                      .error("Ping service unavailable", cause);
                throw new RuntimeException("Ping service unavailable: " + cause.getMessage());
            }
            
            @Override
            public PingTargetDTO stopMonitoring(UUID deviceId) {
                logger.with("operation", "stopMonitoring")
                      .with("deviceId", deviceId)
                      .error("Ping service unavailable", cause);
                throw new RuntimeException("Ping service unavailable: " + cause.getMessage());
            }
            
            @Override
            public void deletePingTarget(UUID id) {
                logger.with("operation", "deletePingTarget")
                      .with("id", id)
                      .error("Ping service unavailable", cause);
                throw new RuntimeException("Ping service unavailable: " + cause.getMessage());
            }
            
            @Override
            public List<PingResultDTO> getPingResults(UUID deviceId, int limit) {
                logger.with("operation", "getPingResults")
                      .with("deviceId", deviceId)
                      .error("Ping service unavailable", cause);
                return new ArrayList<>();
            }

            @Override
            public List<PingResultDTO> getPingResultsSince(UUID deviceId, int minutes) {
                logger.with("operation", "getPingResultsSince")
                      .with("deviceId", deviceId)
                      .with("minutes", minutes)
                      .error("Ping service unavailable", cause);
                return new ArrayList<>();
            }
            
            @Override
            public PingStatisticsDTO getPingStatistics(UUID deviceId, int minutes) {
                logger.with("operation", "getPingStatistics")
                      .with("deviceId", deviceId)
                      .with("minutes", minutes)
                      .error("Ping service unavailable", cause);
                // Return default statistics
                return PingStatisticsDTO.builder()
                    .deviceId(deviceId)
                    .successRate(0.0)
                    .averageRtt(0.0)
                    .recentFailures(0)
                    .totalSamples(0)
                    .uptime(0.0)
                    .packetLoss(100.0)
                    .build();
            }
            
            @Override
            public List<PingTargetDTO> getPingTargetsBatch(List<UUID> deviceIds) {
                logger.with("operation", "getPingTargetsBatch")
                      .with("deviceCount", deviceIds.size())
                      .error("Ping service unavailable", cause);
                return new ArrayList<>();
            }
        };
    }
}