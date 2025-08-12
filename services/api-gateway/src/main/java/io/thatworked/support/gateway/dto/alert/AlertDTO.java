package io.thatworked.support.gateway.dto.alert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertDTO {
    private UUID id;
    private UUID deviceId;
    private String deviceName;
    private String alertType;
    private String message;
    private Instant timestamp;
    private boolean resolved;
    private Instant resolvedAt;
    private boolean acknowledged;
    private Instant acknowledgedAt;
    private String acknowledgedBy;
    private Instant createdAt;
    private Instant updatedAt;
}