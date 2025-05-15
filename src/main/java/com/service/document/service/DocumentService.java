package com.service.document.service;

import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.DocumentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.Collection;

public interface DocumentService {

    DocumentResponseDTO create(
        DocumentCreateDTO createDTO,
        MultipartFile file,
        Long ownerId,
        Collection<Long> departmentIds
    );

    Page<DocumentResponseDTO> list(
        Collection<Long> departmentIds,
        Pageable pageable
    );

    void delete(
        Long id,
        Long userId,
        Collection<Long> departmentIds
    );

    Page<DocumentResponseDTO> search(
        String keyword,
        Collection<Long> departmentIds,
        Pageable pageable
    );

    DocumentResponseDTO update(
        Long id,
        DocumentCreateDTO updateDTO,
        MultipartFile file,
        Long ownerId,
        Collection<Long> departmentIds
    );

    DocumentResponseDTO update(
        Long id,
        DocumentUpdateDTO dto
    );

    DocumentResponseDTO getDocumentById(Long id);
}