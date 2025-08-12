package io.thatworked.support.device.api.dto.request;

import lombok.Data;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class BulkUpdateRequest {
    private List<UUID> deviceIds;
    private Map<String, String> updates;
}