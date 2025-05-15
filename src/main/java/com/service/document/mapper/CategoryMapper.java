package com.service.document.mapper;

import com.service.document.dto.CategoryResponseDTO;
import com.service.document.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDTO toDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .createdBy(category.getCreatedBy())
                .build();
    }
}
