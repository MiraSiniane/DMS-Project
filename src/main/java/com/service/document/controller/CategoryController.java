package com.service.document.controller;

import com.service.document.dto.*;
import com.service.document.service.CategoryService;
import com.service.document.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


import jakarta.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Create a new category
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestBody @Valid CategoryCreateDTO dto,
            Authentication authentication
    ) {
        Long userId = jwtTokenUtil.getUserId(authentication);
        CategoryResponseDTO response = categoryService.create(dto, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * List all categories with pagination
     */
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CategoryResponseDTO> categories = categoryService.listAll(pageRequest);
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(
            @PathVariable Long id
    ) {
        CategoryResponseDTO dto = categoryService.getById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Update category
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDTO dto,
            Authentication authentication
    ) {
        Long userId = jwtTokenUtil.getUserId(authentication);
        CategoryResponseDTO updated = categoryService.update(id, dto, userId);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete category
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Long userId = jwtTokenUtil.getUserId(authentication);
        categoryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
public ResponseEntity<Page<CategoryResponseDTO>> searchCategories(@RequestParam String q, Pageable pageable) {
    return ResponseEntity.ok(categoryService.search(q, pageable));
}

    /**
     * List documents in a category
     */
    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPERADMIN')")
    public ResponseEntity<Page<DocumentResponseDTO>> getDocumentsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Collection<Long> deptIds = jwtTokenUtil.getDeptIds(authentication);
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<DocumentResponseDTO> documents = categoryService.getDocumentsInCategory(id, deptIds, pageRequest);
        return ResponseEntity.ok(documents);
    }
}