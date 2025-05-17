package com.service.document.repository;

import com.service.document.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** 
     * Search categories by name (case-insensitive, contains).
     */
    @Query("""
      SELECT c 
        FROM Category c 
       WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Category> findByNameContainingIgnoreCase(
        @Param("keyword") String keyword,
        Pageable pageable
    );

    /**
     * Count how many documents belong to any of the given category IDs.
     */
    @Query("""
    SELECT COUNT(d)
      FROM Document d
     WHERE d.category.id IN :categoryIds
  """)
  long countDocumentsByCategoryIds(@Param("categoryIds") Collection<Long> ids);

    
}
