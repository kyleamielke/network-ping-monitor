package io.thatworked.support.device.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    
    @Min(value = 0, message = "Page number must be non-negative")
    @Builder.Default
    private Integer page = 0;
    
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 1000, message = "Page size must not exceed 1000")  // TECH DEBT: See ServiceProperties for scaling notes
    @Builder.Default
    private Integer size = 20;
    
    @Builder.Default
    private String sortBy = "name";
    
    @Builder.Default
    private String sortDirection = "ASC";
    
    public String getSortDirection() {
        return sortDirection != null ? sortDirection.toUpperCase() : "ASC";
    }
    
    public boolean isAscending() {
        return !"DESC".equalsIgnoreCase(sortDirection);
    }
}