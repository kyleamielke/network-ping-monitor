package io.thatworked.support.alert.api.dto.request;

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
public class AlertSearchCriteria {
    private UUID deviceId;
    private AlertType alertType;
    @JsonProperty("resolved")
    private Boolean isResolved;
    @JsonProperty("acknowledged")
    private Boolean isAcknowledged;
    private Instant startTime;
    private Instant endTime;
    
    // Pagination and sorting
    @Builder.Default
    private int page = 0;
    @Builder.Default
    private int size = 20;
    @Builder.Default
    private String sortBy = "timestamp";
    @Builder.Default
    private String sortDirection = "DESC";
}