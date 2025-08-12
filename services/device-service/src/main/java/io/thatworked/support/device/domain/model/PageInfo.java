package io.thatworked.support.device.domain.model;

import lombok.Builder;
import lombok.Data;

/**
 * Domain model for pagination information.
 * Framework-agnostic representation of page request.
 */
@Data
@Builder
public class PageInfo {
    private final int pageNumber;
    private final int pageSize;
    private final String sortBy;
    private final boolean ascending;
    
    public static PageInfo of(int pageNumber, int pageSize) {
        return PageInfo.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .sortBy("id")
            .ascending(true)
            .build();
    }
    
    public static PageInfo of(int pageNumber, int pageSize, String sortBy, boolean ascending) {
        return PageInfo.builder()
            .pageNumber(pageNumber)
            .pageSize(pageSize)
            .sortBy(sortBy)
            .ascending(ascending)
            .build();
    }
}