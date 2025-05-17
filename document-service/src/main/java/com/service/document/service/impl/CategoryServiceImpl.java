package com.service.document.service.impl;

import com.service.document.dto.CategoryCreateDTO;
import com.service.document.dto.CategoryResponseDTO;
import com.service.document.dto.CategoryUpdateDTO;
import com.service.document.entity.Category;
import com.service.document.exception.ResourceNotFoundException;
import com.service.document.mapper.CategoryMapper;
import com.service.document.repository.CategoryRepository;
import com.service.document.repository.DocumentRepository;
import com.service.document.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final DocumentRepository documentRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDTO create(CategoryCreateDTO dto, Long userId) {
        // No Department lookup here
        Category category = new Category();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription()); // if your entity has this field
        Category saved = categoryRepository.save(category);
        return categoryMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> listAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                                 .map(categoryMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getById(Long id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::toResponseDTO)
            .orElseThrow(() ->
                new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryUpdateDTO dto, Long userId) {
        Category existing = categoryRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException("Category not found with id: " + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription()); // if present
        Category updated = categoryRepository.save(existing);
        return categoryMapper.toResponseDTO(updated);
    }

    @Override
    public void delete(Long id, Long userId) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getDocumentCountByCategory(Collection<Long> allowedDeptIds) {
        Map<Long, Long> counts = new HashMap<>();
        categoryRepository.findAll().forEach(cat -> {
            long cnt = documentRepository
                .countByCategoryIdAndDepartmentIdIn(cat.getId(), allowedDeptIds);
            counts.put(cat.getId(), cnt);
        });
        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponseDTO> search(String keyword, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword, pageable)
                                 .map(categoryMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page
      getDocumentsInCategory(Long categoryId,
                             Collection<Long> allowedDeptIds,
                             Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException(
                "Category not found with id: " + categoryId);
        }
        return documentRepository
            .findByCategoryIdAndDepartmentIdIn(categoryId, allowedDeptIds, pageable)
            .map(doc -> com.service.document.mapper.DocumentMapper.toDto(doc));
    }
}
