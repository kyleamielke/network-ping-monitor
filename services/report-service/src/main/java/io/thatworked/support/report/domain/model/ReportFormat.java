package io.thatworked.support.report.domain.model;

/**
 * Enumeration of supported report output formats.
 */
public enum ReportFormat {
    
    PDF("application/pdf", "pdf"),
    CSV("text/csv", "csv");
    
    private final String mimeType;
    private final String fileExtension;
    
    ReportFormat(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }
    
    /**
     * Returns the MIME type for this format.
     */
    public String getMimeType() {
        return mimeType;
    }
    
    /**
     * Returns the file extension for this format.
     */
    public String getFileExtension() {
        return fileExtension;
    }
    
    /**
     * Checks if this format supports structured data (tables, charts, etc.).
     */
    public boolean supportsStructuredData() {
        return this == PDF;
    }
    
    /**
     * Checks if this format is suitable for large datasets.
     */
    public boolean isSuitableForLargeDatasets() {
        return this == CSV;
    }
}