package io.thatworked.support.gateway.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Relay-compliant PageInfo type containing pagination metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private boolean hasNextPage;
    private boolean hasPreviousPage;
    private String startCursor;
    private String endCursor;
}