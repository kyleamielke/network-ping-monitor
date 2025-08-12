package io.thatworked.support.report.infrastructure.repository;

import io.thatworked.support.report.infrastructure.entity.ReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA repository for report entities.
 */
@Repository
public interface JpaReportRepository extends JpaRepository<ReportEntity, UUID> {
    
    Page<ReportEntity> findByReportType(ReportEntity.ReportType reportType, Pageable pageable);
    
    Page<ReportEntity> findByFormat(ReportEntity.ReportFormat format, Pageable pageable);
    
    Page<ReportEntity> findByStatus(ReportEntity.ReportStatus status, Pageable pageable);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.generatedAt BETWEEN :startDate AND :endDate")
    Page<ReportEntity> findByDateRange(@Param("startDate") Instant startDate, 
                                      @Param("endDate") Instant endDate, 
                                      Pageable pageable);
    
    @Query("SELECT r FROM ReportEntity r WHERE r.createdAt < :cutoffDate")
    List<ReportEntity> findOlderThan(@Param("cutoffDate") Instant cutoffDate);
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.reportType = :reportType")
    long countByReportType(@Param("reportType") ReportEntity.ReportType reportType);
    
    @Query("SELECT COUNT(r) FROM ReportEntity r WHERE r.status = :status")
    long countByStatus(@Param("status") ReportEntity.ReportStatus status);
}