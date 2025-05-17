package com.service.document.controller;

import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.DocumentUpdateDTO;
import com.service.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> createDocument(
            @RequestPart("document") @Valid DocumentCreateDTO dto,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        String authHeader = getAuthHeader(jwt);
        DocumentResponseDTO response = documentService.createDocument(dto, file, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponseDTO>> listDocuments(
            @RequestParam(required = false) Long departmentId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {

        String authHeader = getAuthHeader(jwt);
        Page<DocumentResponseDTO> documents;
        
        if (departmentId != null) {
            documents = documentService.getDepartmentDocuments(departmentId, authHeader, pageable);
        } else {
            documents = documentService.getDocumentsByUserDepartments(authHeader, pageable);
        }
        
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDTO> getDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        
        String authHeader = getAuthHeader(jwt);
        DocumentResponseDTO document = documentService.getDocumentById(id, authHeader);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        
        String authHeader = getAuthHeader(jwt);
        documentService.deleteDocument(id, authHeader);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponseDTO>> searchDocuments(
            @RequestParam String q,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        
        String authHeader = getAuthHeader(jwt);
        Page<DocumentResponseDTO> results = documentService.searchDocuments(q, authHeader, pageable);
        return ResponseEntity.ok(results);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DocumentResponseDTO> updateDocumentMetadata(
            @PathVariable Long id,
            @RequestBody @Valid DocumentUpdateDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        String authHeader = getAuthHeader(jwt);
        DocumentResponseDTO updated = documentService.updateDocumentMetadata(id, dto, authHeader);
        return ResponseEntity.ok(updated);
    }

    @PutMapping(value = "/{id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> updateDocumentWithFile(
            @PathVariable Long id,
            @RequestPart("document") @Valid DocumentUpdateDTO dto,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        String authHeader = getAuthHeader(jwt);
        DocumentResponseDTO updated = documentService.updateDocumentWithFile(id, dto, file, authHeader);
        return ResponseEntity.ok(updated);
    }

    private String getAuthHeader(Jwt jwt) {
        // Convert JWT to an authorization header format
        // This is a placeholder implementation - you would need to customize this
        // to match how your service extracts and validates the auth token
        return "Bearer " + jwt.getTokenValue();
    }
}