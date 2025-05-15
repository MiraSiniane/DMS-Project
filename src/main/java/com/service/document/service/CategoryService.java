package com.service.document.service;

import com.service.document.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collection;
import java.util.Map;

public interface CategoryService {
    CategoryResponseDTO create(CategoryCreateDTO dto, Long userId);
    Page<CategoryResponseDTO> listAll(Pageable pageable);
    CategoryResponseDTO getById(Long id);
    CategoryResponseDTO update(Long id, CategoryUpdateDTO dto, Long userId);
    void delete(Long id, Long userId);
    Map<Long, Long> getDocumentCountByCategory(Collection<Long> allowedDeptIds);
    Page<CategoryResponseDTO> search(String keyword, Pageable pageable);
    Page<DocumentResponseDTO> getDocumentsInCategory(Long categoryId, Collection<Long> allowedDeptIds, Pageable pageable);


}
