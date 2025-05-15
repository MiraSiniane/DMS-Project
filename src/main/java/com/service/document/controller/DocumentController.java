package com.service.document.controller;

import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
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

import java.util.Collections;
import java.util.List;

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
        
        Long userId = getUserIdFromJwt(jwt);
        List<Long> departmentIds = Collections.singletonList(dto.getDepartmentId());
        
        DocumentResponseDTO response = documentService.create(dto, file, userId, departmentIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponseDTO>> listDocuments(
            @RequestParam(required = false) List<Long> departmentIds,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal Jwt jwt) {
        
        // If no department IDs are provided, get user's departments from JWT
        if (departmentIds == null || departmentIds.isEmpty()) {
            departmentIds = getDepartmentIdsFromJwt(jwt);
        }
        
        Page<DocumentResponseDTO> documents = documentService.list(departmentIds, pageable);
        return ResponseEntity.ok(documents);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
    documentService.delete(id, getUserIdFromJwt(jwt), getDepartmentIdsFromJwt(jwt));
    return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
      public ResponseEntity<Page<DocumentResponseDTO>> searchDocuments(
        @RequestParam String q,
        @PageableDefault(size = 10) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt) {
        List<Long> departmentIds = getDepartmentIdsFromJwt(jwt);
        return ResponseEntity.ok(documentService.search(q, departmentIds, pageable));
}

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
     public ResponseEntity<DocumentResponseDTO> updateDocument(
        @PathVariable Long id,
        @RequestPart("document") @Valid DocumentCreateDTO dto,
        @RequestPart(value = "file", required = false) MultipartFile file,
        @AuthenticationPrincipal Jwt jwt) {

      Long userId = getUserIdFromJwt(jwt);
      List<Long> departmentIds = Collections.singletonList(dto.getDepartmentId());

      DocumentResponseDTO updated = documentService.update(id, dto, file, userId, departmentIds);
      return ResponseEntity.ok(updated);
}


    
    // Helper method to extract user ID from JWT
    private Long getUserIdFromJwt(Jwt jwt) {
        // In a real app, this would extract from claims
        return 1L; // Mock user ID for now
    }
    
    // Helper method to extract department IDs from JWT
    private List<Long> getDepartmentIdsFromJwt(Jwt jwt) {
        // In a real app, this would extract from claims
        return Collections.singletonList(1L); // Mock department ID for now
    }
}