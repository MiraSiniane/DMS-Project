package com.service.document.mapper;

import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.UserInfoDTO;
import com.service.document.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {

    /**
     * Map a Document + optional UserInfo into a response DTO.
     */
    public DocumentResponseDTO toResponseDTO(Document doc, UserInfoDTO userInfo) {
        if (doc == null) return null;

        // Use no-arg constructor + setters (Lombok @Data gives you these)
        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(doc.getId());
        dto.setTitle(doc.getTitle());
        dto.setTranslatedTitle(doc.getTranslatedTitle());
        // If you want a presigned/download URL here, generate it instead of raw key:
        dto.setFileUrl("/api/documents/download/" + doc.getId());
        dto.setFileType(doc.getContentType());
        dto.setFileSize(doc.getFileSize());
        dto.setCreatedAt(doc.getCreatedAt());
        dto.setCategoryId(doc.getCategory() != null ? doc.getCategory().getId() : null);
        dto.setDepartmentId(doc.getDepartmentId());
        dto.setOwnerId(doc.getUserId());

        if (userInfo != null) {
            dto.setOwner(userInfo.firstName() + " " + userInfo.lastName());
        }

        return dto;
    }

    /**
     * Overload when you don’t have UserInfoDTO (owner info will be null).
     */
    public DocumentResponseDTO toResponseDTO(Document doc) {
        return toResponseDTO(doc, null);
    }

    public static Object toDto(Document doc) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toDto'");
    }
}
