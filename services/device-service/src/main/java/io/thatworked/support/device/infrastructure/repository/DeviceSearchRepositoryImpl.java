package io.thatworked.support.device.infrastructure.repository;

import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.api.dto.request.DeviceSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DeviceSearchRepositoryImpl implements DeviceSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Device> searchDevices(DeviceSearchCriteria criteria, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Device> query = cb.createQuery(Device.class);
        Root<Device> root = query.from(Device.class);

        // Build predicates for the main query
        List<Predicate> predicates = buildPredicatesForRoot(cb, root, criteria);

        // Apply all predicates
        if (!predicates.isEmpty()) {
            query.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        // Apply sorting from Pageable
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(order -> {
                if (order.isAscending()) {
                    orders.add(cb.asc(root.get(order.getProperty())));
                } else {
                    orders.add(cb.desc(root.get(order.getProperty())));
                }
            });
            query.orderBy(orders);
        }

        // Execute query for results
        List<Device> results = entityManager.createQuery(query)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        // Count total results
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Device> countRoot = countQuery.from(Device.class);
        countQuery.select(cb.count(countRoot));
        
        // Rebuild predicates for count query using countRoot
        List<Predicate> countPredicates = buildPredicatesForRoot(cb, countRoot, criteria);
        if (!countPredicates.isEmpty()) {
            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
        }
        
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, totalCount);
    }

    private List<Predicate> buildPredicatesForRoot(CriteriaBuilder cb, Root<Device> root, DeviceSearchCriteria criteria) {
        List<Predicate> predicates = new ArrayList<>();

        // UUID search (exact match)
        if (criteria.getUuid() != null) {
            predicates.add(cb.equal(root.get("id"), criteria.getUuid()));
        }

        // Endpoint ID search (exact match)
        if (criteria.getEndpointId() != null && !criteria.getEndpointId().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("endpointId"), criteria.getEndpointId()));
        }

        // Asset Tag search (exact match)
        if (criteria.getAssetTag() != null && !criteria.getAssetTag().trim().isEmpty()) {
            predicates.add(cb.equal(root.get("assetTag"), criteria.getAssetTag()));
        }

        // Name search (partial match, case-insensitive)
        if (criteria.getName() != null && !criteria.getName().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), 
                "%" + criteria.getName().toLowerCase() + "%"));
        }

        // IP Address search (partial match)
        if (criteria.getIpAddress() != null && !criteria.getIpAddress().trim().isEmpty()) {
            predicates.add(cb.like(root.get("ipAddress"), 
                "%" + criteria.getIpAddress() + "%"));
        }

        // MAC Address search (partial match, case-insensitive)
        if (criteria.getMacAddress() != null && !criteria.getMacAddress().trim().isEmpty()) {
            predicates.add(cb.like(cb.upper(root.get("macAddress")), 
                "%" + criteria.getMacAddress().toUpperCase() + "%"));
        }

        // Device type search (exact match, case-insensitive)
        if (criteria.getDeviceType() != null && !criteria.getDeviceType().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("type")), 
                criteria.getDeviceType().toLowerCase()));
        }

        // OS search (partial match, case-insensitive)
        if (criteria.getOs() != null && !criteria.getOs().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("os")), 
                "%" + criteria.getOs().toLowerCase() + "%"));
        }

        // OS Type search (exact match, case-insensitive)
        if (criteria.getOsType() != null && !criteria.getOsType().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get("osType")), 
                criteria.getOsType().toLowerCase()));
        }

        // Make search (partial match, case-insensitive)
        if (criteria.getMake() != null && !criteria.getMake().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("make")), 
                "%" + criteria.getMake().toLowerCase() + "%"));
        }

        // Model search (partial match, case-insensitive)
        if (criteria.getModel() != null && !criteria.getModel().trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("model")), 
                "%" + criteria.getModel().toLowerCase() + "%"));
        }

        // Site search (exact match)
        if (criteria.getSite() != null) {
            predicates.add(cb.equal(root.get("site"), criteria.getSite()));
        }

        // Assignment status filter
        if (criteria.getIsAssigned() != null) {
            if (criteria.getIsAssigned()) {
                predicates.add(cb.isNotNull(root.get("site")));
            } else {
                predicates.add(cb.isNull(root.get("site")));
            }
        }

        return predicates;
    }
}