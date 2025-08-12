package io.thatworked.support.gateway.dto.monitoring;

import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.dto.device.DeviceDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingTargetDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMonitoringDTO {
    private DeviceDTO device;
    private PingTargetDTO pingTarget;
    private DeviceStatusDTO currentStatus;
    private List<AlertDTO> recentAlerts;
    private Map<String, Object> pingStatistics;
    private List<PingResultDTO> recentPingResults;
}