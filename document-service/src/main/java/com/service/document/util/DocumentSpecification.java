package com.service.document.util;

import com.service.document.entity.Document;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import com.service.document.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;

public class DocumentSpecification {
    public static Specification<Document> filter(
            Collection<Long> deptIds,
            Long categoryId,
            String title,
            String query,
            Date startDate,
            Date endDate
    ) {
        return (root, queryBuilder, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Department access control
            if (deptIds != null && !deptIds.isEmpty()) {
                predicates.add(root.get("departmentId").in(deptIds));
            }
            
            // Category filter
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            
            // Title filter (exact match)
            if (title != null && !title.isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("titleEn")),
                    "%" + title.toLowerCase() + "%"
                ));
            }
            
            // Full-text search across multiple fields
            if (query != null && !query.isBlank()) {
                String searchTerm = "%" + query.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("titleEn")), searchTerm),
                    cb.like(cb.lower(root.get("titleEs")), searchTerm),
                    cb.like(cb.lower(root.get("description")), searchTerm)
                ));
            }
            
            // Date range filter
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                    root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(
                    root.get("createdAt"), endDate));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}