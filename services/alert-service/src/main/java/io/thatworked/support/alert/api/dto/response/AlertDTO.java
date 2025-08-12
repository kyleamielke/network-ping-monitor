package io.thatworked.support.alert.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class AlertDTO {
    private UUID id;
    private UUID deviceId;
    private String deviceName;
    private String alertType;
    private String message;
    private Instant timestamp;
    @JsonProperty("resolved")
    private boolean isResolved;
    private Instant resolvedAt;
    @JsonProperty("acknowledged")
    private boolean isAcknowledged;
    private Instant acknowledgedAt;
    private String acknowledgedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;
}