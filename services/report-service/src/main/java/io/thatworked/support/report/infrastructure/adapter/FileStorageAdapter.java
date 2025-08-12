package io.thatworked.support.report.infrastructure.adapter;

import io.thatworked.support.report.domain.model.ReportContent;
import io.thatworked.support.report.domain.port.FileStoragePort;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Infrastructure adapter for file storage operations.
 */
@Component
public class FileStorageAdapter implements FileStoragePort {
    
    private final String basePath;
    private final String baseUrl;
    private final StructuredLogger logger;
    
    public FileStorageAdapter(@Value("${report.storage.path:/tmp/reports}") String basePath,
                             @Value("${report.storage.base-url:http://localhost:8084/api/v1/reports}") String baseUrl,
                             StructuredLoggerFactory loggerFactory) {
        this.basePath = basePath;
        this.baseUrl = baseUrl;
        this.logger = loggerFactory.getLogger(FileStorageAdapter.class);
        initializeStorageDirectory();
    }
    
    @Override
    public String storeReportFile(UUID reportId, String filename, ReportContent content) {
        try {
            logger.with("operation", "storeReportFile")
                  .with("reportId", reportId.toString())
                  .with("filename", filename)
                  .with("fileSize", content.getSize())
                  .info("Storing report file");
            
            Path filePath = Paths.get(basePath, reportId.toString() + "_" + filename);
            Files.write(filePath, content.getData());
            
            String downloadUrl = generateDownloadUrl(reportId, filename);
            
            logger.with("operation", "storeReportFile")
                  .with("reportId", reportId.toString())
                  .with("filePath", filePath.toString())
                  .with("downloadUrl", downloadUrl)
                  .info("Successfully stored report file");
            
            return downloadUrl;
            
        } catch (IOException e) {
            logger.with("operation", "storeReportFile")
                  .with("reportId", reportId.toString())
                  .with("filename", filename)
                  .error("Failed to store report file", e);
            throw new RuntimeException("Failed to store report file", e);
        }
    }
    
    @Override
    public ReportContent getReportFile(UUID reportId) {
        try {
            logger.with("operation", "getReportFile")
                  .with("reportId", reportId.toString())
                  .info("Retrieving report file");
            
            // Find file with reportId prefix
            Path directory = Paths.get(basePath);
            Path filePath = Files.list(directory)
                    .filter(path -> path.getFileName().toString().startsWith(reportId.toString() + "_"))
                    .findFirst()
                    .orElse(null);
            
            if (filePath == null || !Files.exists(filePath)) {
                logger.with("operation", "getReportFile")
                      .with("reportId", reportId.toString())
                      .warn("Report file not found");
                return null;
            }
            
            byte[] data = Files.readAllBytes(filePath);
            
            logger.with("operation", "getReportFile")
                  .with("reportId", reportId.toString())
                  .with("fileSize", data.length)
                  .info("Successfully retrieved report file");
            
            return ReportContent.of(data);
            
        } catch (IOException e) {
            logger.with("operation", "getReportFile")
                  .with("reportId", reportId.toString())
                  .error("Failed to retrieve report file", e);
            throw new RuntimeException("Failed to retrieve report file", e);
        }
    }
    
    @Override
    public void deleteReportFile(UUID reportId) {
        try {
            logger.with("operation", "deleteReportFile")
                  .with("reportId", reportId.toString())
                  .info("Deleting report file");
            
            Path directory = Paths.get(basePath);
            Files.list(directory)
                    .filter(path -> path.getFileName().toString().startsWith(reportId.toString() + "_"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            logger.with("operation", "deleteReportFile")
                                  .with("reportId", reportId.toString())
                                  .with("filePath", path.toString())
                                  .with("error", e.getMessage())
                                  .warn("Failed to delete report file");
                        }
                    });
            
            logger.with("operation", "deleteReportFile")
                  .with("reportId", reportId.toString())
                  .info("Successfully deleted report file");
            
        } catch (IOException e) {
            logger.with("operation", "deleteReportFile")
                  .with("reportId", reportId.toString())
                  .error("Failed to delete report file", e);
            throw new RuntimeException("Failed to delete report file", e);
        }
    }
    
    @Override
    public boolean reportFileExists(UUID reportId) {
        try {
            Path directory = Paths.get(basePath);
            if (!Files.exists(directory)) {
                return false;
            }
            
            return Files.list(directory)
                    .anyMatch(path -> path.getFileName().toString().startsWith(reportId.toString() + "_"));
                    
        } catch (IOException e) {
            logger.with("operation", "reportFileExists")
                  .with("reportId", reportId.toString())
                  .error("Failed to check if report file exists", e);
            return false;
        }
    }
    
    @Override
    public String generateDownloadUrl(UUID reportId, String filename) {
        return String.format("%s/%s/download", baseUrl, reportId.toString());
    }
    
    @Override
    public byte[] readFile(String filePath) {
        try {
            logger.with("operation", "readFile")
                  .with("filePath", filePath)
                  .info("Reading file");
            
            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                logger.with("operation", "readFile")
                      .with("filePath", filePath)
                      .warn("File not found");
                throw new RuntimeException("File not found: " + filePath);
            }
            
            byte[] data = Files.readAllBytes(path);
            
            logger.with("operation", "readFile")
                  .with("filePath", filePath)
                  .with("fileSize", data.length)
                  .info("Successfully read file");
            
            return data;
            
        } catch (IOException e) {
            logger.with("operation", "readFile")
                  .with("filePath", filePath)
                  .error("Failed to read file", e);
            throw new RuntimeException("Failed to read file: " + filePath, e);
        }
    }
    
    @Override
    public void deleteFile(String filePath) {
        try {
            logger.with("operation", "deleteFile")
                  .with("filePath", filePath)
                  .info("Deleting file");
            
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                logger.with("operation", "deleteFile")
                      .with("filePath", filePath)
                      .info("Successfully deleted file");
            } else {
                logger.with("operation", "deleteFile")
                      .with("filePath", filePath)
                      .warn("File does not exist");
            }
            
        } catch (IOException e) {
            logger.with("operation", "deleteFile")
                  .with("filePath", filePath)
                  .error("Failed to delete file", e);
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }
    
    private void initializeStorageDirectory() {
        try {
            Path path = Paths.get(basePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.with("operation", "initializeStorageDirectory")
                      .with("path", basePath)
                      .info("Created report storage directory");
            }
        } catch (IOException e) {
            logger.with("operation", "initializeStorageDirectory")
                  .with("path", basePath)
                  .error("Failed to create report storage directory", e);
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }
}