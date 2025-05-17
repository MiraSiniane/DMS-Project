package com.service.document.mapper;

import com.service.document.dto.CategoryResponseDTO;
import com.service.document.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toResponseDTO(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryResponseDTO(
            category.getId(),
            category.getName(),
            category.getDescription(), null, null
        );
    }
}
