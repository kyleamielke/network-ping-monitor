package io.thatworked.support.device.infrastructure.persistence.specification;

import io.thatworked.support.device.api.dto.request.DeviceFilter;
import io.thatworked.support.device.infrastructure.entity.Device;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceSpecifications {
    
    public static Specification<Device> withFilter(DeviceFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (filter == null) {
                return criteriaBuilder.conjunction();
            }
            
            // Name filter (case-insensitive contains)
            if (StringUtils.hasText(filter.getName())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), 
                    "%" + filter.getName().toLowerCase() + "%"
                ));
            }
            
            // IP address filter (exact match or contains)
            if (StringUtils.hasText(filter.getIpAddress())) {
                predicates.add(criteriaBuilder.like(
                    root.get("ipAddress"), 
                    "%" + filter.getIpAddress() + "%"
                ));
            }
            
            // Device type filter (exact match)
            if (StringUtils.hasText(filter.getDeviceType())) {
                predicates.add(criteriaBuilder.equal(
                    root.get("deviceType"), 
                    filter.getDeviceType()
                ));
            }
            
            // OS filter (case-insensitive contains)
            if (StringUtils.hasText(filter.getOs())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("os")), 
                    "%" + filter.getOs().toLowerCase() + "%"
                ));
            }
            
            // Make filter (case-insensitive contains)
            if (StringUtils.hasText(filter.getMake())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("make")), 
                    "%" + filter.getMake().toLowerCase() + "%"
                ));
            }
            
            // Model filter (case-insensitive contains)
            if (StringUtils.hasText(filter.getModel())) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("model")), 
                    "%" + filter.getModel().toLowerCase() + "%"
                ));
            }
            
            // Site filter
            if (filter.getSiteId() != null) {
                predicates.add(criteriaBuilder.equal(
                    root.get("siteId"), 
                    filter.getSiteId()
                ));
            }
            
            // Assignment filter
            if (filter.getIsAssigned() != null) {
                if (filter.getIsAssigned()) {
                    predicates.add(criteriaBuilder.isNotNull(root.get("siteId")));
                } else {
                    predicates.add(criteriaBuilder.isNull(root.get("siteId")));
                }
            }
            
            // General search term (searches across multiple fields)
            if (StringUtils.hasText(filter.getSearchTerm())) {
                String searchPattern = "%" + filter.getSearchTerm().toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ipAddress")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("deviceType")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("os")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("make")), searchPattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), searchPattern)
                );
                predicates.add(searchPredicate);
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}