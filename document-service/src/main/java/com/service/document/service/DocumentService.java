package com.service.document.service;

import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.DocumentUpdateDTO;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {
    /**
     * Create a new document with file attachment
     */
    DocumentResponseDTO createDocument(DocumentCreateDTO dto, MultipartFile file, String authHeader);
    
    /**
     * Get documents accessible to user based on their department access
     */
    Page<DocumentResponseDTO> getDocumentsByUserDepartments(String authHeader, Pageable pageable);
    
    /**
     * Get a single document by ID
     */
    DocumentResponseDTO getDocumentById(Long id, String authHeader);
    
    /**
     * Update document metadata only
     */
    DocumentResponseDTO updateDocumentMetadata(Long id, DocumentUpdateDTO dto, String authHeader);
    
    /**
     * Update document including file replacement
     */
    DocumentResponseDTO updateDocumentWithFile(Long id, DocumentUpdateDTO dto, MultipartFile file, String authHeader);
    
    /**
     * Delete a document
     */
    void deleteDocument(Long id, String authHeader);
    
    /**
     * Search documents by keyword
     */
    Page<DocumentResponseDTO> searchDocuments(String keyword, String authHeader, Pageable pageable);
    
    /**
     * Get documents for a specific department
     */
    Page<DocumentResponseDTO> getDepartmentDocuments(Long departmentId, String authHeader, Pageable pageable);
}