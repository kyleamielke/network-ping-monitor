package io.thatworked.support.search.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for search operations.
 */
public class SearchRequest {
    
    @NotBlank(message = "Query cannot be blank")
    @Size(min = 2, max = 500, message = "Query must be between 2 and 500 characters")
    private String query;
    
    @Min(value = 1, message = "Limit must be at least 1")
    private Integer limit = 10;
    
    private String type;
    
    public SearchRequest() {}
    
    public SearchRequest(String query, Integer limit, String type) {
        this.query = query;
        this.limit = limit;
        this.type = type;
    }
    
    // Getters and Setters
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Integer getLimit() {
        return limit;
    }
    
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}