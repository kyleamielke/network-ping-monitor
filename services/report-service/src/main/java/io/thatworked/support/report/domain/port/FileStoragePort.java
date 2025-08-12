package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.ReportContent;
import java.util.UUID;

/**
 * Port for file storage operations.
 */
public interface FileStoragePort {
    
    /**
     * Stores a report file and returns the download URL.
     */
    String storeReportFile(UUID reportId, String filename, ReportContent content);
    
    /**
     * Retrieves a report file by ID.
     */
    ReportContent getReportFile(UUID reportId);
    
    /**
     * Deletes a report file.
     */
    void deleteReportFile(UUID reportId);
    
    /**
     * Checks if a report file exists.
     */
    boolean reportFileExists(UUID reportId);
    
    /**
     * Generates a download URL for a report file.
     */
    String generateDownloadUrl(UUID reportId, String filename);
    
    /**
     * Reads a file from the given path.
     */
    byte[] readFile(String filePath);
    
    /**
     * Deletes a file at the given path.
     */
    void deleteFile(String filePath);
}