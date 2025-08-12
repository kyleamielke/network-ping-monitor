package io.thatworked.support.report.infrastructure.repository;

import io.thatworked.support.report.infrastructure.entity.ReportEntity;
import io.thatworked.support.report.infrastructure.entity.ScheduledReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA repository for scheduled report entities.
 */
@Repository
public interface JpaScheduledReportRepository extends JpaRepository<ScheduledReportEntity, UUID> {
    
    List<ScheduledReportEntity> findByIsActive(boolean isActive);
    
    List<ScheduledReportEntity> findByReportType(ReportEntity.ReportType reportType);
    
    @Query("SELECT s FROM ScheduledReportEntity s WHERE s.isActive = true AND s.nextRunTime <= :currentTime")
    List<ScheduledReportEntity> findDueReports(@Param("currentTime") Instant currentTime);
    
    @Query("SELECT COUNT(s) FROM ScheduledReportEntity s WHERE s.isActive = true")
    long countActiveSchedules();
}