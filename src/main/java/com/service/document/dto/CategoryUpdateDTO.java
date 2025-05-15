package com.service.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryUpdateDTO {
    @NotBlank
    private String name;
    private String description;
}