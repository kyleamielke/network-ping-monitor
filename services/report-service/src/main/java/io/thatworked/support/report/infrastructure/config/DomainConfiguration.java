package io.thatworked.support.report.infrastructure.config;

import io.thatworked.support.report.domain.service.ReportDomainService;
import io.thatworked.support.report.domain.service.ReportDocumentBuilder;
import io.thatworked.support.report.domain.port.*;
import io.thatworked.support.report.application.usecase.*;
import io.thatworked.support.report.application.service.ReportApplicationService;
import io.thatworked.support.report.domain.port.ReportRepository;
import io.thatworked.support.report.domain.port.ScheduledReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain services and application layer.
 * Wires up clean architecture components.
 */
@Configuration
public class DomainConfiguration {
    
    @Bean
    public ReportDocumentBuilder reportDocumentBuilder() {
        return new ReportDocumentBuilder();
    }
    
    @Bean
    public ReportDomainService reportDomainService(DeviceDataPort deviceDataPort,
                                                  PingDataPort pingDataPort,
                                                  AlertDataPort alertDataPort,
                                                  ReportGeneratorPort reportGeneratorPort,
                                                  FileStoragePort fileStoragePort,
                                                  DomainLogger domainLogger) {
        return new ReportDomainService(
            deviceDataPort,
            pingDataPort,
            alertDataPort,
            reportGeneratorPort,
            fileStoragePort,
            domainLogger
        );
    }
    
    @Bean
    public GenerateReportUseCase generateReportUseCase(ReportDomainService reportDomainService,
                                                      ReportRepository reportRepository,
                                                      DomainLogger domainLogger) {
        return new GenerateReportUseCase(reportDomainService, reportRepository, domainLogger);
    }
    
    @Bean
    public GetReportByIdUseCase getReportByIdUseCase(ReportRepository reportRepository) {
        return new GetReportByIdUseCase(reportRepository);
    }
    
    @Bean
    public ListReportsUseCase listReportsUseCase(ReportRepository reportRepository,
                                                DomainLogger domainLogger) {
        return new ListReportsUseCase(reportRepository, domainLogger);
    }
    
    @Bean
    public DeleteReportUseCase deleteReportUseCase(ReportRepository reportRepository,
                                                  FileStoragePort fileStoragePort,
                                                  DomainLogger domainLogger) {
        return new DeleteReportUseCase(reportRepository, fileStoragePort, domainLogger);
    }
    
    @Bean
    public ScheduleReportUseCase scheduleReportUseCase(ScheduledReportRepository scheduledReportRepository,
                                                      DomainLogger domainLogger) {
        return new ScheduleReportUseCase(scheduledReportRepository, domainLogger);
    }
    
    @Bean
    public GetReportStatusUseCase getReportStatusUseCase(ReportRepository reportRepository,
                                                        DomainLogger domainLogger) {
        return new GetReportStatusUseCase(reportRepository, domainLogger);
    }
    
    @Bean
    public DownloadReportUseCase downloadReportUseCase(ReportRepository reportRepository,
                                                      FileStoragePort fileStoragePort,
                                                      DomainLogger domainLogger) {
        return new DownloadReportUseCase(reportRepository, fileStoragePort, domainLogger);
    }
    
    @Bean
    public RegenerateReportUseCase regenerateReportUseCase(ReportRepository reportRepository,
                                                          ReportGeneratorPort reportGeneratorPort,
                                                          GenerateReportUseCase generateReportUseCase,
                                                          DomainLogger domainLogger) {
        return new RegenerateReportUseCase(reportRepository, reportGeneratorPort, 
                                          generateReportUseCase, domainLogger);
    }
    
    @Bean
    public CleanupOldReportsUseCase cleanupOldReportsUseCase(ReportRepository reportRepository,
                                                            FileStoragePort fileStoragePort,
                                                            DomainLogger domainLogger,
                                                            @Value("${report.retention.days:30}") int retentionDays) {
        return new CleanupOldReportsUseCase(reportRepository, fileStoragePort, domainLogger, retentionDays);
    }
    
    @Bean
    public ReportApplicationService reportApplicationService(GenerateReportUseCase generateReportUseCase,
                                                           GetReportByIdUseCase getReportByIdUseCase,
                                                           ListReportsUseCase listReportsUseCase,
                                                           DeleteReportUseCase deleteReportUseCase,
                                                           DownloadReportUseCase downloadReportUseCase) {
        return new ReportApplicationService(generateReportUseCase, getReportByIdUseCase,
                                           listReportsUseCase, deleteReportUseCase, downloadReportUseCase);
    }
}