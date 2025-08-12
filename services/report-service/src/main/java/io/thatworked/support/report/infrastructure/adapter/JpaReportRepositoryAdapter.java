package io.thatworked.support.report.infrastructure.adapter;

import io.thatworked.support.report.domain.model.Report;
import io.thatworked.support.report.domain.port.ReportRepository;
import io.thatworked.support.report.domain.model.ReportType;
import io.thatworked.support.report.domain.model.ReportFormat;
import io.thatworked.support.report.infrastructure.entity.ReportEntity;
import io.thatworked.support.report.infrastructure.mapper.ReportEntityMapper;
import io.thatworked.support.report.infrastructure.repository.JpaReportRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA-based implementation of ReportRepository.
 */
@Repository
@Primary
public class JpaReportRepositoryAdapter implements ReportRepository {
    
    private final JpaReportRepository jpaRepository;
    private final ReportEntityMapper mapper;
    
    public JpaReportRepositoryAdapter(JpaReportRepository jpaRepository, 
                                     ReportEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Report save(Report report) {
        ReportEntity entity = mapper.toEntity(report);
        ReportEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Report> findById(UUID reportId) {
        return jpaRepository.findById(reportId)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Report> findReports(ReportType type, ReportFormat format, 
                                  Instant startDate, Instant endDate, int limit) {
        // Simple implementation - can be optimized with custom queries
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "generatedAt"));
        List<Report> allReports = jpaRepository.findAll(pageable).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
        
        return allReports.stream()
            .filter(report -> type == null || report.getReportType().equals(type))
            .filter(report -> format == null || report.getFormat().equals(format))
            .filter(report -> startDate == null || !report.getGeneratedAt().isBefore(startDate))
            .filter(report -> endDate == null || !report.getGeneratedAt().isAfter(endDate))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Report> findReportsOlderThan(Instant cutoffDate) {
        return jpaRepository.findOlderThan(cutoffDate).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void delete(UUID reportId) {
        jpaRepository.deleteById(reportId);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
}