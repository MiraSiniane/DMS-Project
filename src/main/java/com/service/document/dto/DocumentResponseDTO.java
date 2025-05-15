package com.service.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseDTO {
    private Long id;
    private String titleEn;
    private String titleEs;
    private String description;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private String departmentName;
    private String departmentDescription;
    private Long departmentId;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    
    private Instant createdAt;
    private Instant updatedAt;
}