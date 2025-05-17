package com.service.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateDTO {
    @NotBlank
    private String name;
    private String description;
    public Long getDepartmentId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDepartmentId'");
    }
}