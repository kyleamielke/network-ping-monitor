package io.thatworked.support.report.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Service
public class FileStorageService {
    
    private final StructuredLogger logger;
    
    @Value("${report.storage.path:/tmp/reports}")
    private String storageBasePath;
    
    @Value("${report.download.base-url:http://localhost:8084/api/reports/download}")
    private String downloadBaseUrl;
    
    public FileStorageService(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(FileStorageService.class);
    }
    
    public String storeReportFile(String reportId, String filename, byte[] content) {
        try {
            // Ensure storage directory exists
            Path storageDir = Paths.get(storageBasePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
                logger.with("operation", "createStorageDirectory")
                        .with("directory", storageDir.toString())
                        .info("Created storage directory");
            }
            
            // Store the file
            Path filePath = storageDir.resolve(filename);
            Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            logger.with("operation", "storeReportFile")
                    .with("reportId", reportId)
                    .with("filename", filename)
                    .with("size", content.length)
                    .info("Stored report file successfully");
            
            // Return download URL
            return String.format("%s/%s", downloadBaseUrl, filename);
            
        } catch (IOException e) {
            logger.with("operation", "storeReportFile")
                    .with("filename", filename)
                    .with("error", e.getMessage())
                    .error("Failed to store report file", e);
            throw new RuntimeException("File storage failed", e);
        }
    }
    
    public byte[] retrieveReportFile(String filename) {
        try {
            Path filePath = Paths.get(storageBasePath, filename);
            if (!Files.exists(filePath)) {
                logger.with("operation", "retrieveReportFile")
                        .with("filename", filename)
                        .warn("Report file not found");
                return null;
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            logger.with("operation", "retrieveReportFile")
                    .with("filename", filename)
                    .with("error", e.getMessage())
                    .error("Failed to retrieve report file", e);
            throw new RuntimeException("File retrieval failed", e);
        }
    }
    
    public boolean deleteReportFile(String filename) {
        try {
            Path filePath = Paths.get(storageBasePath, filename);
            boolean deleted = Files.deleteIfExists(filePath);
            
            if (deleted) {
                logger.with("operation", "deleteReportFile")
                        .with("filename", filename)
                        .info("Deleted report file successfully");
            } else {
                logger.with("operation", "deleteReportFile")
                        .with("filename", filename)
                        .warn("Report file not found for deletion");
            }
            
            return deleted;
            
        } catch (IOException e) {
            logger.with("operation", "deleteReportFile")
                    .with("filename", filename)
                    .with("error", e.getMessage())
                    .error("Failed to delete report file", e);
            return false;
        }
    }
}