package io.thatworked.support.alert.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "Device ID is required")
    private UUID deviceId;
    
    @NotBlank(message = "Device name is required")
    private String deviceName;
    
    @NotNull(message = "Alert type is required")
    private AlertType alertType;
    
    @NotBlank(message = "Message is required")
    private String message;
}