package io.thatworked.support.gateway.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Device status DTO from dashboard-cache-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceStatusDTO {
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private Instant lastStatusChange;
    private Double responseTime;
    private int consecutiveSuccesses;
    private int consecutiveFailures;
    private String status; // e.g. "OFFLINE", "ONLINE"
    private boolean online;
}