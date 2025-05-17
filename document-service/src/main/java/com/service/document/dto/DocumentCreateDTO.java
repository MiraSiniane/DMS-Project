package com.service.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentCreateDTO(
    @NotBlank String title,
    String translatedTitle,
    @NotNull Long departmentId,
    @NotNull Long categoryId
) {

    public Object description() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'description'");
    }}