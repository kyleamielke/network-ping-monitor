package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.PingServiceClient;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.monitoring.DeviceMonitoringDTO;
import io.thatworked.support.gateway.dto.ping.PingStatisticsDTO;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Field resolver for DeviceMonitoring type
 */
@Controller
public class DeviceMonitoringFieldResolver {
    
    private final StructuredLogger logger;
    private final PingServiceClient pingServiceClient;
    
    public DeviceMonitoringFieldResolver(StructuredLoggerFactory loggerFactory,
                                       PingServiceClient pingServiceClient) {
        this.logger = loggerFactory.getLogger(DeviceMonitoringFieldResolver.class);
        this.pingServiceClient = pingServiceClient;
    }
    
    @SchemaMapping(typeName = "DeviceMonitoring", field = "recentAlerts")
    public List<AlertDTO> recentAlerts(DeviceMonitoringDTO monitoring, @Argument Integer limit) {
        logger.with("operation", "deviceMonitoring.recentAlerts")
              .with("deviceId", monitoring.getDevice().getId())
              .with("limit", limit)
              .debug("Resolving recentAlerts field");
        
        if (monitoring.getRecentAlerts() == null || monitoring.getRecentAlerts().isEmpty()) {
            return List.of();
        }
        
        List<AlertDTO> alerts = monitoring.getRecentAlerts();
        
        // Apply limit if specified
        if (limit != null && limit > 0 && alerts.size() > limit) {
            return alerts.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        return alerts;
    }
    
    @SchemaMapping(typeName = "DeviceMonitoring", field = "statistics")
    public Map<String, Object> statistics(DeviceMonitoringDTO monitoring, @Argument String timeRange) {
        logger.with("operation", "deviceMonitoring.statistics")
              .with("deviceId", monitoring.getDevice().getId())
              .with("timeRange", timeRange)
              .debug("Resolving statistics field");
        
        try {
            // Fetch statistics from ping service
            // Convert timeRange to minutes (default to 60 minutes for LAST_24_HOURS)
            int minutes = timeRangeToMinutes(timeRange);
            PingStatisticsDTO pingStats = pingServiceClient.getPingStatistics(
                monitoring.getDevice().getId(), minutes);
            
            // Convert to Map for GraphQL
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPings", pingStats.getTotalSamples());
            stats.put("successfulPings", pingStats.getSuccessfulPings());
            stats.put("failedPings", pingStats.getFailedPings());
            stats.put("successRate", pingStats.getSuccessRate());
            stats.put("averageResponseTime", pingStats.getAverageRtt());
            stats.put("minResponseTime", 0.0); // Not provided by ping service yet
            stats.put("maxResponseTime", 0.0); // Not provided by ping service yet
            stats.put("uptime", pingStats.getUptime());
            stats.put("packetLoss", pingStats.getPacketLoss());
            
            return stats;
        } catch (Exception e) {
            logger.with("operation", "deviceMonitoring.statistics")
                  .with("deviceId", monitoring.getDevice().getId())
                  .with("error", e.getMessage())
                  .error("Failed to fetch ping statistics", e);
            
            // Return default statistics on error
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPings", 0);
            stats.put("successfulPings", 0);
            stats.put("failedPings", 0);
            stats.put("successRate", 0.0);
            stats.put("averageResponseTime", 0.0);
            stats.put("minResponseTime", 0.0);
            stats.put("maxResponseTime", 0.0);
            stats.put("uptime", 0.0);
            stats.put("packetLoss", 0.0);
            return stats;
        }
    }
    
    private int timeRangeToMinutes(String timeRange) {
        if (timeRange == null) return 60;
        
        // Parse the enum value to extract the duration
        // Format: LAST_N_UNIT (e.g., LAST_24_HOURS, LAST_7_DAYS)
        String[] parts = timeRange.split("_");
        if (parts.length < 3 || !parts[0].equals("LAST")) {
            return 60; // Default to 1 hour
        }
        
        try {
            int value = Integer.parseInt(parts[1]);
            String unit = parts[2];
            
            switch (unit) {
                case "HOUR":
                case "HOURS":
                    return value * 60;
                case "DAY":
                case "DAYS":
                    return value * 24 * 60;
                case "WEEK":
                case "WEEKS":
                    return value * 7 * 24 * 60;
                default:
                    return 60; // Default to 1 hour
            }
        } catch (NumberFormatException e) {
            logger.with("operation", "timeRangeToMinutes")
                  .with("timeRange", timeRange)
                  .with("error", e.getMessage())
                  .warn("Failed to parse time range, using default");
            return 60;
        }
    }
}