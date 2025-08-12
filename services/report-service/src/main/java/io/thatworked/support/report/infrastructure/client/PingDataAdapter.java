package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.domain.model.ReportTimeRange;
import io.thatworked.support.report.domain.port.PingDataPort;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.report.infrastructure.dto.PingStatisticsDTO;
import io.thatworked.support.report.infrastructure.dto.PingTargetDTO;
import io.thatworked.support.report.infrastructure.dto.PingResultDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter for accessing ping data from the ping service.
 */
@Component
public class PingDataAdapter implements PingDataPort {
    
    private final PingServiceClient pingServiceClient;
    private final PingTargetClient pingTargetClient;
    private final StructuredLogger logger;
    
    public PingDataAdapter(PingServiceClient pingServiceClient, 
                          PingTargetClient pingTargetClient,
                          StructuredLoggerFactory loggerFactory) {
        this.pingServiceClient = pingServiceClient;
        this.pingTargetClient = pingTargetClient;
        this.logger = loggerFactory.getLogger(PingDataAdapter.class);
    }
    
    @Override
    public List<PingStatistics> getAllDeviceStatistics(ReportTimeRange timeRange) {
        try {
            logger.with("operation", "getAllDeviceStatistics")
                  .with("startDate", timeRange.getStartDate())
                  .with("endDate", timeRange.getEndDate())
                  .info("Fetching ping statistics from ping service");
            
            List<PingStatisticsDTO> statistics = pingServiceClient.getAllDeviceStatistics(
                timeRange.getStartDate().toString(), 
                timeRange.getEndDate().toString()
            );
            
            logger.with("operation", "getAllDeviceStatistics")
                  .with("statisticsCount", statistics.size())
                  .info("Successfully fetched ping statistics from ping service");
            
            return statistics.stream()
                    .map(this::mapToPingStatistics)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.with("operation", "getAllDeviceStatistics")
                  .error("Failed to fetch ping statistics from ping service", e);
            throw new RuntimeException("Failed to fetch ping statistics", e);
        }
    }
    
    @Override
    public List<PingStatistics> getDeviceStatistics(List<UUID> deviceIds, ReportTimeRange timeRange) {
        // For simplicity, fetch all and filter - could be optimized with specific endpoint
        List<PingStatistics> allStats = getAllDeviceStatistics(timeRange);
        
        return allStats.stream()
                .filter(stat -> deviceIds.contains(stat.deviceId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PingTarget> getAllPingTargets() {
        try {
            logger.with("operation", "getAllPingTargets")
                  .info("Fetching ping targets from ping service");
            
            List<PingTargetDTO> targets = pingTargetClient.getAllPingTargets();
            
            logger.with("operation", "getAllPingTargets")
                  .with("targetCount", targets.size())
                  .info("Successfully fetched ping targets from ping service");
            
            return targets.stream()
                    .map(this::mapToPingTarget)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.with("operation", "getAllPingTargets")
                  .error("Failed to fetch ping targets from ping service", e);
            throw new RuntimeException("Failed to fetch ping targets", e);
        }
    }
    
    @Override
    public List<PingResult> getRecentPingResults(UUID deviceId, int limit) {
        try {
            logger.with("operation", "getRecentPingResults")
                  .with("deviceId", deviceId.toString())
                  .with("limit", limit)
                  .info("Fetching recent ping results from ping service");
            
            List<PingResultDTO> results = pingTargetClient.getPingResults(
                deviceId, 
                limit
            );
            
            logger.with("operation", "getRecentPingResults")
                  .with("deviceId", deviceId.toString())
                  .with("resultCount", results.size())
                  .info("Successfully fetched recent ping results from ping service");
            
            return results.stream()
                    .map(dto -> mapToPingResult(dto, deviceId))
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.with("operation", "getRecentPingResults")
                  .with("deviceId", deviceId.toString())
                  .error("Failed to fetch recent ping results from ping service", e);
            throw new RuntimeException("Failed to fetch recent ping results", e);
        }
    }
    
    private PingStatistics mapToPingStatistics(PingStatisticsDTO dto) {
        return new PingStatistics(
            UUID.fromString(dto.getDeviceId()),
            dto.getDeviceName(),
            dto.getTargetIp(),
            dto.getTargetHostname(),
            dto.getTotalPings(),
            dto.getSuccessfulPings(),
            dto.getFailedPings(),
            dto.getUptimePercentage(),
            dto.getAverageResponseTime() != null ? dto.getAverageResponseTime() : 0.0,
            dto.getMinResponseTime() != null ? dto.getMinResponseTime() : 0.0,
            dto.getMaxResponseTime() != null ? dto.getMaxResponseTime() : 0.0,
            dto.getPeriodStart().atZone(java.time.ZoneId.systemDefault()).toInstant(),
            dto.getPeriodEnd().atZone(java.time.ZoneId.systemDefault()).toInstant()
        );
    }
    
    private PingTarget mapToPingTarget(PingTargetDTO dto) {
        return new PingTarget(
            dto.getDeviceId(),
            dto.isMonitored(),
            dto.getPingIntervalSeconds() != null ? dto.getPingIntervalSeconds() : 5,
            5000 // Default timeout since not available in DTO
        );
    }
    
    private PingResult mapToPingResult(PingResultDTO dto, UUID deviceId) {
        return new PingResult(
            deviceId,
            dto.getTimestamp(),
            dto.isSuccess(),
            dto.getResponseTimeMs() != null ? dto.getResponseTimeMs().doubleValue() : 0.0,
            dto.getErrorMessage()
        );
    }
}