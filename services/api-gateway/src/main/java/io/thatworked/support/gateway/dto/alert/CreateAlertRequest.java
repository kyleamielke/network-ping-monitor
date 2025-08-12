package io.thatworked.support.gateway.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRequest {
    private UUID deviceId;
    private String deviceName;
    private String alertType;
    private String message;
}