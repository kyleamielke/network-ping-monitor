package io.thatworked.support.ping.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.thatworked.support.ping.domain.PingResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PingResultDTO {
    
    private String deviceId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;
    
    private Long responseTimeMs;
    private boolean success;
    private String errorMessage;
    
    public static PingResultDTO fromDomain(PingResult pingResult) {
        PingResultDTO dto = new PingResultDTO();
        dto.setDeviceId(pingResult.getDeviceId().toString());
        dto.setTimestamp(pingResult.getTime());
        
        // Convert response time from Double to Long (milliseconds)
        if (pingResult.getRoundTripTime() != null) {
            dto.setResponseTimeMs(Math.round(pingResult.getRoundTripTime()));
        }
        
        // Convert status enum to boolean success
        dto.setSuccess(pingResult.getStatus().isSuccess());
        
        if (!pingResult.getStatus().isSuccess()) {
            dto.setErrorMessage(pingResult.getStatus().getDisplayName());
        }
        
        return dto;
    }
}