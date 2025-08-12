package io.thatworked.support.search.infrastructure.dto;

import io.thatworked.support.search.domain.model.SearchType;
import java.time.Instant;
import java.util.Map;

public class SearchItemDTO {
    private String id;
    private SearchType type;
    private String title;
    private String subtitle;
    private String description;
    private Map<String, Object> metadata;
    private double relevanceScore;
    private Instant lastUpdated;

    public SearchItemDTO() {}

    public SearchItemDTO(String id, SearchType type, String title, String subtitle) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.relevanceScore = 1.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}