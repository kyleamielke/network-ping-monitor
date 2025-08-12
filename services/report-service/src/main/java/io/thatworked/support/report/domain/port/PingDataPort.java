package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.ReportTimeRange;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Port for accessing ping monitoring data from external services.
 */
public interface PingDataPort {
    
    /**
     * Retrieves ping statistics for all devices within the specified time range.
     */
    List<PingStatistics> getAllDeviceStatistics(ReportTimeRange timeRange);
    
    /**
     * Retrieves ping statistics for specific devices within the time range.
     */
    List<PingStatistics> getDeviceStatistics(List<UUID> deviceIds, ReportTimeRange timeRange);
    
    /**
     * Retrieves all ping targets to determine monitoring status.
     */
    List<PingTarget> getAllPingTargets();
    
    /**
     * Retrieves recent ping results for a specific device.
     */
    List<PingResult> getRecentPingResults(UUID deviceId, int limit);
    
    /**
     * Data transfer object for ping statistics.
     */
    record PingStatistics(
        UUID deviceId,
        String deviceName,
        String ipAddress,
        String hostname,
        long totalPings,
        long successfulPings,
        long failedPings,
        double uptimePercentage,
        double averageResponseTime,
        double minResponseTime,
        double maxResponseTime,
        Instant firstPing,
        Instant lastPing
    ) {}
    
    /**
     * Data transfer object for ping targets.
     */
    record PingTarget(
        UUID deviceId,
        boolean isMonitored,
        int intervalSeconds,
        int timeoutMs
    ) {}
    
    /**
     * Data transfer object for individual ping results.
     */
    record PingResult(
        UUID deviceId,
        Instant timestamp,
        boolean isSuccess,
        double responseTimeMs,
        String errorMessage
    ) {}
}