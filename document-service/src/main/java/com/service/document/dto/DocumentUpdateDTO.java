package com.service.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentUpdateDTO(
    @NotBlank String title,
    String translatedTitle,
    @NotNull Long categoryId,
    @NotNull Long departmentId
) { }
