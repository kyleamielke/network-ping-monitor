package io.thatworked.support.report.infrastructure.adapter;

import io.thatworked.support.report.domain.model.ScheduledReport;
import io.thatworked.support.report.domain.port.ScheduledReportRepository;
import io.thatworked.support.report.infrastructure.entity.ScheduledReportEntity;
import io.thatworked.support.report.infrastructure.mapper.ScheduledReportEntityMapper;
import io.thatworked.support.report.infrastructure.repository.JpaScheduledReportRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA-based implementation of ScheduledReportRepository.
 */
@Repository
@Primary
public class JpaScheduledReportRepositoryAdapter implements ScheduledReportRepository {
    
    private final JpaScheduledReportRepository jpaRepository;
    private final ScheduledReportEntityMapper mapper;
    
    public JpaScheduledReportRepositoryAdapter(JpaScheduledReportRepository jpaRepository,
                                              ScheduledReportEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public ScheduledReport save(ScheduledReport scheduledReport) {
        ScheduledReportEntity entity = mapper.toEntity(scheduledReport);
        ScheduledReportEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<ScheduledReport> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<ScheduledReport> findActiveReports() {
        return jpaRepository.findByIsActive(true).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ScheduledReport> findReportsDueForExecution(Instant beforeTime) {
        return jpaRepository.findDueReports(beforeTime).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}