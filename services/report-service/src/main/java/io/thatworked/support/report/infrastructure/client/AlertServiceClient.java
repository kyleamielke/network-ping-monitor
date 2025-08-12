package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.AlertDTO;
import io.thatworked.support.report.infrastructure.dto.AlertSearchCriteria;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "alert-service", contextId = "alertServiceClient")
public interface AlertServiceClient {
    
    @PostMapping("/api/v1/alerts/search")
    Page<AlertDTO> searchAlerts(@RequestBody AlertSearchCriteria criteria);
    
    @GetMapping("/api/v1/alerts")
    Page<AlertDTO> getAllAlerts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "1000") int size,
        @RequestParam(defaultValue = "timestamp") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDirection);
}