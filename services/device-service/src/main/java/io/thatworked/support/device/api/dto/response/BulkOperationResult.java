package io.thatworked.support.device.api.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class BulkOperationResult {
    private int totalRequested;
    private int successful;
    private int failed;
    private List<String> errors;
}