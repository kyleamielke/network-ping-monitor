package io.thatworked.support.gateway.event;

import io.thatworked.support.gateway.dto.alert.AlertDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEvent {
    private String eventType; // "alert-created", "alert-resolved", "alert-acknowledged"
    private AlertDTO alert;
}