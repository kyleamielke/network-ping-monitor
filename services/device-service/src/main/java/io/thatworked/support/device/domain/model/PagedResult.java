package io.thatworked.support.device.domain.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Domain model for paginated results.
 * Framework-agnostic representation of paged data.
 */
@Data
@Builder
public class PagedResult<T> {
    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;
    
    public static <T> PagedResult<T> of(List<T> content, PageInfo pageInfo, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageInfo.getPageSize());
        
        return PagedResult.<T>builder()
            .content(content)
            .pageNumber(pageInfo.getPageNumber())
            .pageSize(pageInfo.getPageSize())
            .totalElements(totalElements)
            .totalPages(totalPages)
            .hasNext(pageInfo.getPageNumber() < totalPages - 1)
            .hasPrevious(pageInfo.getPageNumber() > 0)
            .build();
    }
}