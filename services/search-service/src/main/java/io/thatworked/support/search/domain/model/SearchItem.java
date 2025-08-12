package io.thatworked.support.search.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing a single search result item.
 * Immutable value object containing item details and metadata.
 */
public class SearchItem {
    
    private final String id;
    private final SearchType type;
    private final String title;
    private final String summary;
    private final String description;
    private final double relevanceScore;
    private final Map<String, Object> metadata;
    private final Instant lastUpdated;
    
    private SearchItem(Builder builder) {
        this.id = Objects.requireNonNull(builder.id, "ID cannot be null");
        this.type = Objects.requireNonNull(builder.type, "Type cannot be null");
        this.title = Objects.requireNonNull(builder.title, "Title cannot be null");
        this.summary = builder.summary;
        this.description = builder.description;
        this.relevanceScore = builder.relevanceScore;
        this.metadata = Collections.unmodifiableMap(new HashMap<>(builder.metadata));
        this.lastUpdated = builder.lastUpdated;
        
        validateRelevanceScore(this.relevanceScore);
    }
    
    private static void validateRelevanceScore(double score) {
        if (score < 0.0 || score > 1.0) {
            throw new IllegalArgumentException("Relevance score must be between 0.0 and 1.0");
        }
    }
    
    /**
     * Creates a builder for SearchItem.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public SearchType getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getSummary() {
        return summary;
    }
    
    public String getDescription() {
        return description;
    }
    
    public double getRelevanceScore() {
        return relevanceScore;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    /**
     * Gets a specific metadata value.
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetadataValue(String key, Class<T> type) {
        Object value = metadata.get(key);
        if (value == null) {
            return null;
        }
        
        if (!type.isInstance(value)) {
            throw new ClassCastException(
                String.format("Metadata value for key '%s' is not of type %s", key, type.getName())
            );
        }
        
        return (T) value;
    }
    
    /**
     * Checks if this item matches a search query.
     * This is a simple implementation that can be enhanced with more sophisticated matching.
     */
    public boolean matchesQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        
        String lowerQuery = query.toLowerCase();
        
        return (title != null && title.toLowerCase().contains(lowerQuery)) ||
               (summary != null && summary.toLowerCase().contains(lowerQuery)) ||
               (description != null && description.toLowerCase().contains(lowerQuery));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchItem that = (SearchItem) o;
        return Objects.equals(id, that.id) && type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
    
    @Override
    public String toString() {
        return String.format("SearchItem{id='%s', type=%s, title='%s', relevanceScore=%.2f}",
            id, type, title, relevanceScore);
    }
    
    /**
     * Builder for SearchItem.
     */
    public static class Builder {
        private String id;
        private SearchType type;
        private String title;
        private String summary;
        private String description;
        private double relevanceScore = 0.5;
        private Map<String, Object> metadata = new HashMap<>();
        private Instant lastUpdated = Instant.now();
        
        private Builder() {}
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder type(SearchType type) {
            this.type = type;
            return this;
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder relevanceScore(double relevanceScore) {
            this.relevanceScore = relevanceScore;
            return this;
        }
        
        public Builder metadata(Map<String, Object> metadata) {
            if (metadata != null) {
                this.metadata = new HashMap<>(metadata);
            }
            return this;
        }
        
        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public Builder lastUpdated(Instant lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        
        public SearchItem build() {
            return new SearchItem(this);
        }
    }
}