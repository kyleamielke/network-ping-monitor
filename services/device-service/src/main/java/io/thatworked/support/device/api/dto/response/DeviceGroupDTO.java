package io.thatworked.support.device.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeviceGroupDTO {
    private List<DeviceDTO> networkDevices;
    private List<DeviceDTO> servers;
    private List<DeviceDTO> otherDevices;
}