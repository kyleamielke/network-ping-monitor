package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.*;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.common.PageResponse;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.monitoring.DeviceMonitoringDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import io.thatworked.support.gateway.dto.device.DeviceSearchCriteria;
import graphql.schema.DataFetchingEnvironment;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
public class QueryResolver {
    
    private final StructuredLogger logger;
    private final DeviceServiceClient deviceServiceClient;
    private final PingServiceClient pingServiceClient;
    private final AlertServiceClient alertServiceClient;
    
    public QueryResolver(StructuredLoggerFactory loggerFactory,
                        DeviceServiceClient deviceServiceClient,
                        PingServiceClient pingServiceClient,
                        AlertServiceClient alertServiceClient) {
        this.logger = loggerFactory.getLogger(QueryResolver.class);
        this.deviceServiceClient = deviceServiceClient;
        this.pingServiceClient = pingServiceClient;
        this.alertServiceClient = alertServiceClient;
    }
    
    @QueryMapping
    public List<DeviceDTO> devices() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "devices")
              .with("query", "devices")
              .debug("Starting GraphQL query");
        
        try {
            PageResponse<DeviceDTO> response = deviceServiceClient.getDevices(0, 100);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "devices")
                  .with("query", "devices")
                  .with("deviceCount", response.getContent().size())
                  .with("totalElements", response.getTotalElements())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return response.getContent();
        } catch (Exception e) {
            logger.with("operation", "devices")
                  .with("query", "devices")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public DeviceDTO device(@Argument String id) {
        logger.with("operation", "device")
              .with("query", "device")
              .with("deviceId", id)
              .debug("Starting GraphQL query");
        
        try {
            UUID deviceId = UUID.fromString(id);
            DeviceDTO device = deviceServiceClient.getDevice(deviceId);
            
            logger.with("operation", "device")
                  .with("query", "device")
                  .with("deviceId", id)
                  .with("deviceFound", device != null)
                  .info("GraphQL query completed");
            
            return device;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "device")
                  .with("query", "device")
                  .with("deviceId", id)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid device ID format", e);
            throw new IllegalArgumentException("Invalid device ID format: " + id, e);
        } catch (Exception e) {
            logger.with("operation", "device")
                  .with("query", "device")
                  .with("deviceId", id)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("Failed to fetch device", e);
            throw e;
        }
    }
    
    @QueryMapping
    public CompletableFuture<DeviceMonitoringDTO> deviceMonitoring(@Argument String deviceId, DataFetchingEnvironment env) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "deviceMonitoring")
              .with("query", "deviceMonitoring")
              .with("deviceId", deviceId)
              .debug("Starting GraphQL query with DataLoader");
        
        try {
            UUID id = UUID.fromString(deviceId);
            
            // Use DataLoader to batch load device monitoring data
            DataLoader<UUID, DeviceMonitoringDTO> dataLoader = env.getDataLoader("deviceMonitoringLoader");
            CompletableFuture<DeviceMonitoringDTO> future = dataLoader.load(id);
            
            // Log completion asynchronously
            future.whenComplete((result, error) -> {
                long duration = System.currentTimeMillis() - startTime;
                if (error != null) {
                    logger.with("operation", "deviceMonitoring")
                          .with("query", "deviceMonitoring")
                          .with("deviceId", deviceId)
                          .with("errorType", error.getClass().getSimpleName())
                          .with("errorMessage", error.getMessage())
                          .with("durationMs", duration)
                          .error("GraphQL query failed", error);
                } else {
                    logger.with("operation", "deviceMonitoring")
                          .with("query", "deviceMonitoring")
                          .with("deviceId", deviceId)
                          .with("hasResult", result != null)
                          .with("durationMs", duration)
                          .info("GraphQL query completed successfully");
                }
            });
            
            return future;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "deviceMonitoring")
                  .with("query", "deviceMonitoring")
                  .with("deviceId", deviceId)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid device ID format", e);
            throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
        }
    }
    
    @QueryMapping
    public Map<String, Object> monitoringDashboard() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "monitoringDashboard")
              .with("query", "monitoringDashboard")
              .debug("Starting GraphQL query");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
        
        // Get device counts
        PageResponse<DeviceDTO> devices = deviceServiceClient.getDevices(0, 1);
        dashboard.put("totalDevices", devices.getTotalElements());
        
        // Get monitored device count and status
        List<PingTargetDTO> targets = pingServiceClient.getActivePingTargets();
        dashboard.put("monitoredDevices", targets.size());
        
        // Calculate online/offline counts from ping results
        long onlineCount = 0;
        long offlineCount = 0;
        
        for (PingTargetDTO target : targets) {
            try {
                // Get the latest ping results for this device
                List<PingResultDTO> results = pingServiceClient.getPingResults(target.getDeviceId(), 1);
                if (!results.isEmpty() && results.get(0).isSuccess()) {
                    onlineCount++;
                } else {
                    offlineCount++;
                }
            } catch (Exception e) {
                // If we can't get results, count as offline
                offlineCount++;
                logger.with("deviceId", target.getDeviceId())
                      .with("error", e.getMessage())
                      .warn("Failed to get ping results, counting as offline");
            }
        }
        
        dashboard.put("onlineDevices", onlineCount);
        dashboard.put("offlineDevices", offlineCount);
        
        // Get alert counts
        PageResponse<AlertDTO> alerts = alertServiceClient.getAlerts(0, 1);
        dashboard.put("totalAlerts", alerts.getTotalElements());
        
        // TODO: Get unresolved alert count when endpoint is fixed
        dashboard.put("unresolvedAlerts", 0);
        
        // System health
        Map<String, Object> systemHealth = new HashMap<>();
        systemHealth.put("apiGateway", createServiceHealth("api-gateway", "UP"));
        systemHealth.put("deviceService", createServiceHealth("device-service", "UP"));
        systemHealth.put("pingService", createServiceHealth("ping-service", "UP"));
        systemHealth.put("alertService", createServiceHealth("alert-service", "UP"));
        dashboard.put("systemHealth", systemHealth);
        
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "monitoringDashboard")
                  .with("query", "monitoringDashboard")
                  .with("totalDevices", dashboard.get("totalDevices"))
                  .with("onlineDevices", dashboard.get("onlineDevices"))
                  .with("offlineDevices", dashboard.get("offlineDevices"))
                  .with("monitoredDevices", dashboard.get("monitoredDevices"))
                  .with("totalAlerts", dashboard.get("totalAlerts"))
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return dashboard;
        } catch (Exception e) {
            logger.with("operation", "monitoringDashboard")
                  .with("query", "monitoringDashboard")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public List<AlertDTO> alerts() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "alerts")
              .with("query", "alerts")
              .debug("Starting GraphQL query");
        
        try {
            PageResponse<AlertDTO> response = alertServiceClient.getAlerts(0, 100);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "alerts")
                  .with("query", "alerts")
                  .with("alertCount", response.getContent().size())
                  .with("totalElements", response.getTotalElements())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return response.getContent();
        } catch (Exception e) {
            logger.with("operation", "alerts")
                  .with("query", "alerts")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public AlertDTO alert(@Argument String id) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "alert")
              .with("query", "alert")
              .with("alertId", id)
              .debug("Starting GraphQL query");
        
        try {
            UUID alertId = UUID.fromString(id);
            AlertDTO alert = alertServiceClient.getAlert(alertId);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "alert")
                  .with("query", "alert")
                  .with("alertId", id)
                  .with("alertFound", alert != null)
                  .with("durationMs", duration)
                  .info("GraphQL query completed");
            
            return alert;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "alert")
                  .with("query", "alert")
                  .with("alertId", id)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid alert ID format", e);
            throw new IllegalArgumentException("Invalid alert ID format: " + id, e);
        } catch (Exception e) {
            logger.with("operation", "alert")
                  .with("query", "alert")
                  .with("alertId", id)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            return null;
        }
    }
    
    @QueryMapping
    public List<AlertDTO> deviceAlerts(@Argument String deviceId) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "deviceAlerts")
              .with("query", "deviceAlerts")
              .with("deviceId", deviceId)
              .debug("Starting GraphQL query");
        
        try {
            UUID id = UUID.fromString(deviceId);
            List<AlertDTO> alerts = alertServiceClient.getDeviceAlerts(id);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "deviceAlerts")
                  .with("query", "deviceAlerts")
                  .with("deviceId", deviceId)
                  .with("alertCount", alerts.size())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return alerts;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "deviceAlerts")
                  .with("query", "deviceAlerts")
                  .with("deviceId", deviceId)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid device ID format", e);
            throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
        } catch (Exception e) {
            logger.with("operation", "deviceAlerts")
                  .with("query", "deviceAlerts")
                  .with("deviceId", deviceId)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public List<AlertDTO> unresolvedAlerts() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "unresolvedAlerts")
              .with("query", "unresolvedAlerts")
              .debug("Starting GraphQL query");
        
        try {
            // Filter unresolved alerts since the endpoint has issues
            PageResponse<AlertDTO> allAlerts = alertServiceClient.getAlerts(0, 100);
            List<AlertDTO> unresolvedAlerts = allAlerts.getContent().stream()
                .filter(alert -> !alert.isResolved())
                .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "unresolvedAlerts")
                  .with("query", "unresolvedAlerts")
                  .with("totalAlerts", allAlerts.getContent().size())
                  .with("unresolvedCount", unresolvedAlerts.size())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return unresolvedAlerts;
        } catch (Exception e) {
            logger.with("operation", "unresolvedAlerts")
                  .with("query", "unresolvedAlerts")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public Map<String, Object> searchDevices(@Argument Map<String, Object> criteria) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "searchDevices")
              .with("query", "searchDevices")
              .with("criteria", criteria)
              .debug("Starting GraphQL query");
        
        try {
            // Build search criteria
        DeviceSearchCriteria searchCriteria = DeviceSearchCriteria.builder()
            .name((String) criteria.get("name"))
            .ipAddress((String) criteria.get("ipAddress"))
            .type((String) criteria.get("type"))
            .online(criteria.get("online") != null ? (Boolean) criteria.get("online") : null)
            .page(criteria.get("page") != null ? (Integer) criteria.get("page") : 0)
            .size(criteria.get("size") != null ? (Integer) criteria.get("size") : 20)
            .build();
        
        PageResponse<DeviceDTO> response = deviceServiceClient.searchDevices(searchCriteria);
        
        Map<String, Object> result = new HashMap<>();
        result.put("devices", response.getContent());
        result.put("totalElements", response.getTotalElements());
        result.put("totalPages", response.getTotalPages());
        result.put("currentPage", response.getPage());
        result.put("pageSize", response.getSize());
        result.put("hasNext", response.getPage() < response.getTotalPages() - 1);
        result.put("hasPrevious", response.getPage() > 0);
        
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "searchDevices")
                  .with("query", "searchDevices")
                  .with("resultCount", response.getContent().size())
                  .with("totalElements", response.getTotalElements())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return result;
        } catch (Exception e) {
            logger.with("operation", "searchDevices")
                  .with("query", "searchDevices")
                  .with("criteria", criteria)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public List<DeviceDTO> devicesWithMonitoring() {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "devicesWithMonitoring")
              .with("query", "devicesWithMonitoring")
              .debug("Starting GraphQL query");
        
        try {
            // TECH DEBT: Loading all devices at once for client-side pagination
            // This approach works fine for <500 devices but will not scale well
            // Future improvements for large-scale deployments:
            // 1. Implement server-side pagination with prefetching (next/prev pages cached)
            // 2. Use cursor-based pagination for better performance with large datasets
            // 3. Consider virtual scrolling for 5000+ devices
            // 4. Implement Redis caching layer for frequently accessed pages
            // Current limit increased to 1000 to support ~200 devices in academic environment
            PageResponse<DeviceDTO> response = deviceServiceClient.getDevices(0, 1000);
            long duration = System.currentTimeMillis() - startTime;
            
            logger.with("operation", "devicesWithMonitoring")
                  .with("query", "devicesWithMonitoring")
                  .with("deviceCount", response.getContent().size())
                  .with("totalElements", response.getTotalElements())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return response.getContent();
        } catch (Exception e) {
            logger.with("operation", "devicesWithMonitoring")
                  .with("query", "devicesWithMonitoring")
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    @QueryMapping
    public Map<String, Object> pingHistory(@Argument String deviceId, @Argument String timeRange) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "pingHistory")
              .with("query", "pingHistory")
              .with("deviceId", deviceId)
              .with("timeRange", timeRange)
              .debug("Starting GraphQL query");
        
        try {
            UUID id = UUID.fromString(deviceId);
        
            // Map TimeRange enum to minutes for backward compatibility
            int minutes = switch (timeRange) {
                case "LAST_HOUR" -> 60;
                case "LAST_24_HOURS" -> 1440;
                case "LAST_7_DAYS" -> 10080;
                case "LAST_30_DAYS" -> 43200;
                default -> 60; // Default to 1 hour
            };
            
            List<PingResultDTO> results = pingServiceClient.getPingResultsSince(id, minutes);
        
            Map<String, Object> history = new HashMap<>();
            history.put("deviceId", deviceId);
            history.put("results", results);
            history.put("statistics", calculatePingStatistics(results));
        
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "pingHistory")
                  .with("query", "pingHistory")
                  .with("deviceId", deviceId)
                  .with("resultCount", results.size())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return history;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "pingHistory")
                  .with("query", "pingHistory")
                  .with("deviceId", deviceId)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid device ID format", e);
            throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
        } catch (Exception e) {
            logger.with("operation", "pingHistory")
                  .with("query", "pingHistory")
                  .with("deviceId", deviceId)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }

    @QueryMapping
    public Map<String, Object> pingHistoryByTime(@Argument String deviceId, @Argument Map<String, Object> timeRange) {
        long startTime = System.currentTimeMillis();
        logger.with("operation", "pingHistoryByTime")
              .with("query", "pingHistoryByTime")
              .with("deviceId", deviceId)
              .with("timeRange", timeRange)
              .debug("Starting GraphQL query");
        
        try {
            UUID id = UUID.fromString(deviceId);
        
            // Calculate total minutes from TimeRangeInput
            int totalMinutes = 0;
            if (timeRange.get("minutes") != null) {
                totalMinutes += (Integer) timeRange.get("minutes");
            }
            if (timeRange.get("hours") != null) {
                totalMinutes += (Integer) timeRange.get("hours") * 60;
            }
            if (timeRange.get("days") != null) {
                totalMinutes += (Integer) timeRange.get("days") * 1440;
            }
            
            // Default to 60 minutes if no time specified
            if (totalMinutes == 0) {
                totalMinutes = 60;
            }
            
            List<PingResultDTO> results = pingServiceClient.getPingResultsSince(id, totalMinutes);
        
            Map<String, Object> history = new HashMap<>();
            history.put("deviceId", deviceId);
            history.put("results", results);
            history.put("statistics", calculatePingStatistics(results));
        
            long duration = System.currentTimeMillis() - startTime;
            logger.with("operation", "pingHistoryByTime")
                  .with("query", "pingHistoryByTime")
                  .with("deviceId", deviceId)
                  .with("totalMinutes", totalMinutes)
                  .with("resultCount", results.size())
                  .with("durationMs", duration)
                  .info("GraphQL query completed successfully");
            
            return history;
        } catch (IllegalArgumentException e) {
            logger.with("operation", "pingHistoryByTime")
                  .with("query", "pingHistoryByTime")
                  .with("deviceId", deviceId)
                  .with("errorType", "INVALID_UUID")
                  .error("Invalid device ID format", e);
            throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
        } catch (Exception e) {
            logger.with("operation", "pingHistoryByTime")
                  .with("query", "pingHistoryByTime")
                  .with("deviceId", deviceId)
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("GraphQL query failed", e);
            throw e;
        }
    }
    
    private Map<String, Object> calculatePingStatistics(List<PingResultDTO> results) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalPings = results.size();
        long successfulPings = results.stream().filter(PingResultDTO::isSuccess).count();
        long failedPings = totalPings - successfulPings;
        
        stats.put("totalPings", totalPings);
        stats.put("successfulPings", successfulPings);
        stats.put("failedPings", failedPings);
        
        double successRate = totalPings > 0 ? (double) successfulPings / totalPings : 0.0;
        stats.put("successRate", successRate);
        stats.put("uptime", successRate * 100); // Uptime as percentage
        stats.put("packetLoss", totalPings > 0 ? ((double) failedPings / totalPings) * 100 : 0.0);
        
        List<Long> responseTimes = results.stream()
            .filter(r -> r.isSuccess() && r.getResponseTimeMs() != null)
            .map(PingResultDTO::getResponseTimeMs)
            .collect(Collectors.toList());
        
        if (!responseTimes.isEmpty()) {
            double avgResponse = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
            stats.put("averageResponseTime", avgResponse);
            stats.put("avgResponseTime", avgResponse); // Alias for frontend compatibility
            stats.put("minResponseTime", Collections.min(responseTimes));
            stats.put("maxResponseTime", Collections.max(responseTimes));
        } else {
            stats.put("averageResponseTime", 0.0);
            stats.put("avgResponseTime", 0.0);
            stats.put("minResponseTime", 0);
            stats.put("maxResponseTime", 0);
        }
        
        return stats;
    }
    
    private Map<String, Object> createServiceHealth(String name, String status) {
        Map<String, Object> health = new HashMap<>();
        health.put("name", name);
        health.put("status", status);
        health.put("message", null);
        return health;
    }
}