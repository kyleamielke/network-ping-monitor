package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.AlertServiceClient;
import io.thatworked.support.gateway.client.DeviceServiceClient;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.common.*;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.util.CursorUtil;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GraphQL resolver for cursor-based pagination queries.
 * Implements Relay-compliant connections.
 */
@Controller
public class ConnectionResolver {
    
    private final StructuredLogger logger;
    private final DeviceServiceClient deviceServiceClient;
    private final AlertServiceClient alertServiceClient;
    
    public ConnectionResolver(StructuredLoggerFactory loggerFactory,
                            DeviceServiceClient deviceServiceClient,
                            AlertServiceClient alertServiceClient) {
        this.logger = loggerFactory.getLogger(ConnectionResolver.class);
        this.deviceServiceClient = deviceServiceClient;
        this.alertServiceClient = alertServiceClient;
    }
    
    @QueryMapping
    public Connection<DeviceDTO> devicesConnection(
            @Argument Integer first,
            @Argument String after,
            @Argument Integer last,
            @Argument String before,
            @Argument Map<String, Object> filter) {
        
        logger.with("operation", "devicesConnection")
              .with("first", first)
              .with("after", after)
              .with("last", last)
              .with("before", before)
              .info("Fetching devices connection");
        
        // Validate pagination arguments
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
        }
        
        if ((first != null || after != null) && (last != null || before != null)) {
            throw new IllegalArgumentException("Cannot mix forward and backward pagination");
        }
        
        // Default to forward pagination
        boolean isForward = last == null;
        int limit = isForward ? (first != null ? first : 20) : (last != null ? last : 20);
        limit = Math.min(limit, 100); // Cap at 100
        
        // For now, use offset-based pagination internally
        // In a real implementation, you'd use the cursor to determine the starting point
        int page = 0;
        if (after != null) {
            String afterId = CursorUtil.extractId(after);
            // In real implementation, find the position of this ID and start after it
            page = 1; // Simplified for now
        }
        
        // Fetch data
        PageResponse<DeviceDTO> response = deviceServiceClient.getDevices(page, limit + 1);
        List<DeviceDTO> devices = response.getContent();
        
        boolean hasMore = devices.size() > limit;
        if (hasMore) {
            devices = devices.subList(0, limit);
        }
        
        // Create edges with cursors
        List<Edge<DeviceDTO>> edges = devices.stream()
            .map(device -> Edge.<DeviceDTO>builder()
                .node(device)
                .cursor(CursorUtil.encodeCursor("Device", device.getId().toString()))
                .build())
            .collect(Collectors.toList());
        
        // Build page info
        PageInfo pageInfo = PageInfo.builder()
            .hasNextPage(hasMore)
            .hasPreviousPage(page > 0)
            .startCursor(edges.isEmpty() ? null : edges.get(0).getCursor())
            .endCursor(edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor())
            .build();
        
        return Connection.<DeviceDTO>builder()
            .edges(edges)
            .pageInfo(pageInfo)
            .totalCount(response.getTotalElements())
            .build();
    }
    
    @QueryMapping
    public Connection<AlertDTO> alertsConnection(
            @Argument Integer first,
            @Argument String after,
            @Argument Integer last,
            @Argument String before,
            @Argument Map<String, Object> filter) {
        
        logger.with("operation", "alertsConnection")
              .with("first", first)
              .with("after", after)
              .with("last", last)
              .with("before", before)
              .info("Fetching alerts connection");
        
        // Validate pagination arguments
        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
        }
        
        if ((first != null || after != null) && (last != null || before != null)) {
            throw new IllegalArgumentException("Cannot mix forward and backward pagination");
        }
        
        // Default to forward pagination
        boolean isForward = last == null;
        int limit = isForward ? (first != null ? first : 20) : (last != null ? last : 20);
        limit = Math.min(limit, 100); // Cap at 100
        
        // For now, use offset-based pagination internally
        int page = 0;
        if (after != null) {
            String afterId = CursorUtil.extractId(after);
            // In real implementation, find the position of this ID and start after it
            page = 1; // Simplified for now
        }
        
        // Fetch data
        PageResponse<AlertDTO> response = alertServiceClient.getAlerts(page, limit + 1);
        List<AlertDTO> alerts = response.getContent();
        
        boolean hasMore = alerts.size() > limit;
        if (hasMore) {
            alerts = alerts.subList(0, limit);
        }
        
        // Apply filters if provided
        if (filter != null) {
            if (filter.containsKey("resolved")) {
                boolean resolved = (Boolean) filter.get("resolved");
                alerts = alerts.stream()
                    .filter(alert -> alert.isResolved() == resolved)
                    .collect(Collectors.toList());
            }
            if (filter.containsKey("deviceId")) {
                UUID deviceId = UUID.fromString((String) filter.get("deviceId"));
                alerts = alerts.stream()
                    .filter(alert -> alert.getDeviceId().equals(deviceId))
                    .collect(Collectors.toList());
            }
        }
        
        // Create edges with cursors
        List<Edge<AlertDTO>> edges = alerts.stream()
            .map(alert -> Edge.<AlertDTO>builder()
                .node(alert)
                .cursor(CursorUtil.encodeCursor("Alert", alert.getId().toString(), 
                    alert.getTimestamp().toEpochMilli()))
                .build())
            .collect(Collectors.toList());
        
        // Build page info
        PageInfo pageInfo = PageInfo.builder()
            .hasNextPage(hasMore)
            .hasPreviousPage(page > 0)
            .startCursor(edges.isEmpty() ? null : edges.get(0).getCursor())
            .endCursor(edges.isEmpty() ? null : edges.get(edges.size() - 1).getCursor())
            .build();
        
        return Connection.<AlertDTO>builder()
            .edges(edges)
            .pageInfo(pageInfo)
            .totalCount(response.getTotalElements())
            .build();
    }
}