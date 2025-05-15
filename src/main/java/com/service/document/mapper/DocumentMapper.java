package com.service.document.mapper;

import com.service.document.dto.DocumentResponseDTO;
import com.service.document.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    
    public DocumentResponseDTO toDTO(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .titleEn(document.getTitleEn())
                .titleEs(document.getTitleEs())
                .description(document.getDescription())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .categoryId(document.getCategory().getId())
                .departmentId(document.getDepartment().getId())
                .ownerId(document.getOwnerId())
                .createdAt(document.getCreatedAt())
                .build();
    }
}