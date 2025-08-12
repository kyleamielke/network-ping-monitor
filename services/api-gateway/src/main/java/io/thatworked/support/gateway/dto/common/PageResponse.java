package io.thatworked.support.gateway.dto.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response from services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResponse<T> {
    @JsonAlias({"content", "devices"})  // Some services use "devices" instead of "content"
    private List<T> content;
    @JsonAlias({"page", "currentPage"})
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;
    
    // Convenience constructor for fallback responses
    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.hasNext = false;
        this.hasPrevious = false;
        this.first = true;
        this.last = true;
    }
}