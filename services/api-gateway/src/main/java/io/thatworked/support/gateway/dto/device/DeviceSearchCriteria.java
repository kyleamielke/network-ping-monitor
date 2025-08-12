package io.thatworked.support.gateway.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSearchCriteria {
    private String name;
    private String ipAddress;
    private String type;
    private Boolean online;
    private Integer page;
    private Integer size;
}