package io.thatworked.support.report.domain.model;

/**
 * Value object containing metadata about a generated report.
 */
public class ReportMetadata {
    
    private final long fileSizeBytes;
    private final String downloadUrl;
    private final int recordCount;
    private final String generationDuration;
    private final String filePath;
    private final Long processingTimeMs;
    private final String error;
    
    private ReportMetadata(long fileSizeBytes, String downloadUrl, int recordCount, 
                          String generationDuration, String filePath, Long processingTimeMs, String error) {
        this.fileSizeBytes = Math.max(0, fileSizeBytes);
        this.downloadUrl = downloadUrl;
        this.recordCount = Math.max(0, recordCount);
        this.generationDuration = generationDuration;
        this.filePath = filePath;
        this.processingTimeMs = processingTimeMs;
        this.error = error;
    }
    
    /**
     * Creates empty metadata.
     */
    public static ReportMetadata empty() {
        return new ReportMetadata(0, null, 0, null, null, null, null);
    }
    
    /**
     * Creates metadata with file size.
     */
    public static ReportMetadata ofSize(long fileSizeBytes) {
        return new ReportMetadata(fileSizeBytes, null, 0, null, null, null, null);
    }
    
    /**
     * Creates complete metadata.
     */
    public static ReportMetadata of(long fileSizeBytes, String downloadUrl, int recordCount, String generationDuration) {
        return new ReportMetadata(fileSizeBytes, downloadUrl, recordCount, generationDuration, null, null, null);
    }
    
    /**
     * Creates a new metadata instance with updated file size.
     */
    public ReportMetadata withSize(long fileSizeBytes) {
        return new ReportMetadata(fileSizeBytes, this.downloadUrl, this.recordCount, this.generationDuration,
                                this.filePath, this.processingTimeMs, this.error);
    }
    
    /**
     * Creates a new metadata instance with download URL.
     */
    public ReportMetadata withDownloadUrl(String downloadUrl) {
        return new ReportMetadata(this.fileSizeBytes, downloadUrl, this.recordCount, this.generationDuration,
                                this.filePath, this.processingTimeMs, this.error);
    }
    
    /**
     * Creates a new metadata instance with record count.
     */
    public ReportMetadata withRecordCount(int recordCount) {
        return new ReportMetadata(this.fileSizeBytes, this.downloadUrl, recordCount, this.generationDuration,
                                this.filePath, this.processingTimeMs, this.error);
    }
    
    /**
     * Creates a new metadata instance with generation duration.
     */
    public ReportMetadata withGenerationDuration(String generationDuration) {
        return new ReportMetadata(this.fileSizeBytes, this.downloadUrl, this.recordCount, generationDuration,
                                this.filePath, this.processingTimeMs, this.error);
    }
    
    /**
     * Formats file size in human-readable format.
     */
    public String getFormattedFileSize() {
        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", fileSizeBytes / 1024.0);
        } else {
            return String.format("%.1f MB", fileSizeBytes / (1024.0 * 1024.0));
        }
    }
    
    public long getFileSizeBytes() { return fileSizeBytes; }
    public String getDownloadUrl() { return downloadUrl; }
    public int getRecordCount() { return recordCount; }
    public String getGenerationDuration() { return generationDuration; }
    public String getFilePath() { return filePath; }
    public Long getProcessingTimeMs() { return processingTimeMs; }
    public String getError() { return error; }
}