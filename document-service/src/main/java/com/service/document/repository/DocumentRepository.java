package com.service.document.repository;

import com.service.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    Page<Document> findByDepartmentIdIn(Collection<Long> departmentIds, Pageable pageable);
    
    // Single department ID version
    default Page<Document> findByDepartmentIdIn(Long departmentId, Pageable pageable) {
        return findByDepartmentIdIn(List.of(departmentId), pageable);
    }
    
    Page<Document> findByCategoryIdAndDepartmentIdIn(Long categoryId, Collection<Long> deptIds, Pageable pageable);
    
    @Query("SELECT d FROM Document d WHERE " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.translatedTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "d.departmentId IN :deptIds")
    Page<Document> searchByKeywordAndDepartmentIds(@Param("keyword") String keyword,
                                               @Param("deptIds") Collection<Long> departmentIds,
                                               Pageable pageable);
    
    Long countByCategoryIdAndDepartmentIdIn(Long categoryId, Collection<Long> allowedDeptIds);
}