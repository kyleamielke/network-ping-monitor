package io.thatworked.support.report.api.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.report.api.dto.request.*;
import io.thatworked.support.report.api.dto.response.*;
import io.thatworked.support.report.api.mapper.ReportDtoMapper;
import io.thatworked.support.report.application.usecase.*;
import io.thatworked.support.report.domain.exception.ReportNotFoundException;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

/**
 * REST controller for report management operations.
 */
@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final StructuredLogger logger;
    private final GenerateReportUseCase generateReportUseCase;
    private final GetReportByIdUseCase getReportByIdUseCase;
    private final ListReportsUseCase listReportsUseCase;
    private final DeleteReportUseCase deleteReportUseCase;
    private final GetReportStatusUseCase getReportStatusUseCase;
    private final DownloadReportUseCase downloadReportUseCase;
    private final RegenerateReportUseCase regenerateReportUseCase;
    private final ReportDtoMapper mapper;
    
    public ReportController(StructuredLoggerFactory loggerFactory,
                           GenerateReportUseCase generateReportUseCase,
                           GetReportByIdUseCase getReportByIdUseCase,
                           ListReportsUseCase listReportsUseCase,
                           DeleteReportUseCase deleteReportUseCase,
                           GetReportStatusUseCase getReportStatusUseCase,
                           DownloadReportUseCase downloadReportUseCase,
                           RegenerateReportUseCase regenerateReportUseCase,
                           ReportDtoMapper mapper) {
        this.logger = loggerFactory.getLogger(ReportController.class);
        this.generateReportUseCase = generateReportUseCase;
        this.getReportByIdUseCase = getReportByIdUseCase;
        this.listReportsUseCase = listReportsUseCase;
        this.deleteReportUseCase = deleteReportUseCase;
        this.getReportStatusUseCase = getReportStatusUseCase;
        this.downloadReportUseCase = downloadReportUseCase;
        this.regenerateReportUseCase = regenerateReportUseCase;
        this.mapper = mapper;
    }
    
    /**
     * Generates a new report.
     */
    @PostMapping
    public ResponseEntity<ReportResponse> generateReport(@Valid @RequestBody GenerateReportRequest request) {
        logger.with("operation", "generateReport")
              .with("reportType", request.getReportType())
              .with("format", request.getFormat())
              .info("Received request to generate report");
        
        GenerateReportUseCase.ReportRequest ucRequest = mapper.toUseCaseRequest(request);
        GenerateReportUseCase.ReportResponse ucResponse = generateReportUseCase.execute(ucRequest);
        
        ReportResponse response = mapper.toReportResponse(ucResponse);
        
        logger.with("operation", "generateReport")
              .with("reportId", response.getId())
              .with("filename", response.getFilename())
              .info("Successfully generated report");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Gets a report by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@PathVariable String id) {
        logger.with("operation", "getReport")
              .with("reportId", id)
              .info("Received request to get report");
        
        GetReportByIdUseCase.ReportResponse ucResponse = getReportByIdUseCase.execute(id);
        ReportResponse response = mapper.toReportResponse(ucResponse);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lists reports with optional filtering.
     */
    @GetMapping
    public ResponseEntity<ReportListResponse> listReports(@Valid ListReportsRequest request) {
        logger.with("operation", "listReports")
              .with("reportType", request.getReportType())
              .with("limit", request.getLimit())
              .info("Received request to list reports");
        
        ListReportsUseCase.ListReportsRequest ucRequest = new ListReportsUseCase.ListReportsRequest(
            request.getReportType(),
            request.getFormat(),
            request.getStartDate() != null ? request.getStartDate().atZone(ZoneId.systemDefault()).toInstant() : null,
            request.getEndDate() != null ? request.getEndDate().atZone(ZoneId.systemDefault()).toInstant() : null,
            request.getLimit()
        );
        
        ListReportsUseCase.ListReportsResponse ucResponse = listReportsUseCase.execute(ucRequest);
        
        int pageNumber = request.getOffset() / request.getLimit();
        ReportListResponse response = mapper.toReportListResponse(ucResponse, request.getLimit(), pageNumber);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deletes a report.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        logger.with("operation", "deleteReport")
              .with("reportId", id)
              .info("Received request to delete report");
        
        deleteReportUseCase.execute(id);
        
        logger.with("operation", "deleteReport")
              .with("reportId", id)
              .info("Successfully deleted report");
        
        return ResponseEntity.noContent().build();
    }
    
    
    /**
     * Gets the status of a report generation.
     */
    @GetMapping("/{id}/status")
    public ResponseEntity<ReportStatusResponse> getReportStatus(@PathVariable String id) {
        logger.with("operation", "getReportStatus")
              .with("reportId", id)
              .debug("Received request to get report status");
        
        GetReportStatusUseCase.ReportStatusResponse ucResponse = getReportStatusUseCase.execute(id);
        ReportStatusResponse response = mapper.toReportStatusResponse(ucResponse);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Downloads a report file.
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable String id) {
        logger.with("operation", "downloadReport")
              .with("reportId", id)
              .info("Received request to download report");
        
        DownloadReportUseCase.DownloadReportResponse ucResponse = downloadReportUseCase.execute(id);
        
        ByteArrayResource resource = new ByteArrayResource(ucResponse.content());
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + ucResponse.filename() + "\"")
            .contentType(MediaType.parseMediaType(ucResponse.contentType()))
            .contentLength(ucResponse.content().length)
            .body(resource);
    }
    
    /**
     * Regenerates an existing report.
     */
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<ReportResponse> regenerateReport(@PathVariable String id) {
        logger.with("operation", "regenerateReport")
              .with("reportId", id)
              .info("Received request to regenerate report");
        
        GenerateReportUseCase.ReportResponse ucResponse = regenerateReportUseCase.execute(id);
        ReportResponse response = mapper.toReportResponse(ucResponse);
        
        logger.with("operation", "regenerateReport")
              .with("originalReportId", id)
              .with("newReportId", response.getId())
              .info("Successfully regenerated report");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Report service is healthy");
    }
}