package io.thatworked.support.gateway.dto.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertListDTO {
    private List<AlertDTO> alerts;
    private Integer totalCount;
}