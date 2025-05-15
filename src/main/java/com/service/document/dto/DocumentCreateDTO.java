package com.service.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateDTO {
    private String titleEn;
    private String titleEs;
    private String description;
    private Long categoryId;
    private Long departmentId;
    private Long ownerId;
}