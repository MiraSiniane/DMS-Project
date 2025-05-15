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
    Page<Document> findByCategoryIdAndDepartmentIdIn(Long categoryId, Collection<Long> deptIds, Pageable pageable);

    
    
    
    @Query("SELECT d.category, COUNT(d) FROM Document d WHERE d.department.id IN :deptIds GROUP BY d.category")
    List<Object[]> countDocumentsByCategoryAndDepartmentIn(@Param("deptIds") Collection<Long> departmentIds);
    @Query("SELECT d FROM Document d WHERE " +
       "(LOWER(d.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
       "LOWER(d.titleEs) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
       "d.department.id IN :deptIds")
    Page<Document> searchByKeyword(@Param("keyword") String keyword,
                               @Param("deptIds") List<Long> deptIds,
                               Pageable pageable);


}