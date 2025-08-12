package io.thatworked.support.report.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a structured report document that can be rendered in various formats.
 * This is the intermediate representation between raw data and final output.
 */
public record ReportDocument(
    String title,
    String subtitle,
    Instant generatedAt,
    ReportTimeRange timeRange,
    List<Section> sections,
    Map<String, String> metadata
) {
    
    /**
     * Represents a section within the report.
     */
    public record Section(
        String title,
        SectionType type,
        List<String> headers,
        List<Row> rows,
        String summary
    ) {}
    
    /**
     * Represents a row of data in a section.
     */
    public record Row(
        List<Cell> cells
    ) {}
    
    /**
     * Represents a single cell in a row.
     */
    public record Cell(
        String value,
        CellType type,
        String format
    ) {}
    
    /**
     * Types of sections in a report.
     */
    public enum SectionType {
        TABLE,
        SUMMARY,
        CHART_DATA
    }
    
    /**
     * Types of cell content.
     */
    public enum CellType {
        TEXT,
        NUMBER,
        PERCENTAGE,
        CURRENCY,
        DATE,
        BOOLEAN
    }
    
    /**
     * Builder for creating report documents.
     */
    public static class Builder {
        private String title;
        private String subtitle;
        private Instant generatedAt = Instant.now();
        private ReportTimeRange timeRange;
        private final List<Section> sections = new java.util.ArrayList<>();
        private final Map<String, String> metadata = new java.util.HashMap<>();
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }
        
        public Builder timeRange(ReportTimeRange timeRange) {
            this.timeRange = timeRange;
            return this;
        }
        
        public Builder addSection(Section section) {
            this.sections.add(section);
            return this;
        }
        
        public Builder addMetadata(String key, String value) {
            this.metadata.put(key, value);
            return this;
        }
        
        public ReportDocument build() {
            return new ReportDocument(title, subtitle, generatedAt, timeRange, sections, metadata);
        }
    }
}