package com.service.document.service;

import com.service.document.client.StorageClient;
import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.DocumentUpdateDTO;
import com.service.document.entity.Category;
import com.service.document.entity.Department;
import com.service.document.entity.Document;
import com.service.document.event.DocumentCreatedEvent;
import com.service.document.mapper.DocumentMapper;
import com.service.document.repository.CategoryRepository;
import com.service.document.repository.DepartmentRepository;
import com.service.document.repository.DocumentRepository;
import com.service.document.service.impl.DocumentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock private DocumentRepository documentRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private StorageClient storageClient;
    @Mock private KafkaTemplate<String, DocumentCreatedEvent> kafkaTemplate;
    @Mock private DocumentMapper documentMapper;

    @InjectMocks
    private DocumentServiceImpl service;

    private Document sampleDoc;
    private DocumentResponseDTO sampleDTO;

    @BeforeEach
    void setUp() {
        // --- Prepare a sample Document entity
        sampleDoc = new Document();
        sampleDoc.setId(1L);
        sampleDoc.setTitleEn("Test");
        sampleDoc.setTitleEs("TestEs");
        sampleDoc.setDescription("Desc");
        sampleDoc.setFileUrl("http://url/file1.pdf");
        sampleDoc.setFileType("application/pdf");
        sampleDoc.setFileSize(123L);
        sampleDoc.setCreatedAt(Instant.now());
        sampleDoc.setUpdatedAt(Instant.now());
        sampleDoc.setOwnerId(789L);
        sampleDoc.setCategory(new Category(1L, "Cat", "Desc"));
        sampleDoc.setDepartment(new Department(2L, "Dept"));

        // --- Prepare the DTO your mapper should return
        sampleDTO = DocumentResponseDTO.builder()
            .id(sampleDoc.getId())
            .titleEn(sampleDoc.getTitleEn())
            .titleEs(sampleDoc.getTitleEs())
            .description(sampleDoc.getDescription())
            .fileUrl(sampleDoc.getFileUrl())
            .fileType(sampleDoc.getFileType())
            .fileSize(sampleDoc.getFileSize())
            .createdAt(sampleDoc.getCreatedAt())
            .updatedAt(sampleDoc.getUpdatedAt())
            .ownerId(sampleDoc.getOwnerId())
            .categoryId(sampleDoc.getCategory().getId())
            .categoryName(sampleDoc.getCategory().getName())
            .departmentId(sampleDoc.getDepartment().getId())
            .departmentName(sampleDoc.getDepartment().getName())
            .ownerEmail(null)  // or stub an email if your DTO requires it
            .build();

        // --- Stub the mapper once
        when(documentMapper.toDTO(any(Document.class))).thenReturn(sampleDTO);
    }
    @Override
    public DocumentResponseDTO update(Long id, DocumentUpdateDTO dto) {
        Document existing = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found"));

        // apply the DTO changes
        existing.setTitleEn(dto.getTitleEn());
        existing.setTitleEs(dto.getTitleEs());
        existing.setDescription(dto.getDescription());
        existing.setCategory(categoryRepository.findById(dto.getCategoryId())
                                  .orElseThrow(() -> new EntityNotFoundException("Category not found")));
        existing.setDepartment(departmentRepository.findById(dto.getDepartmentId())
                                  .orElseThrow(() -> new EntityNotFoundException("Department not found")));
        existing.setUpdatedAt(Instant.now());

        Document saved = documentRepository.save(existing);
        return documentMapper.toDTO(saved);
    }

    @Test
    void delete_shouldRemoveDocument() {
        // Given the document exists
        when(documentRepository.findById(1L)).thenReturn(Optional.of(sampleDoc));

        // When
        service.delete(1L, 789L, List.of(2L));

        // Then
        verify(documentRepository).deleteById(1L);
    }

    @Test
    void search_shouldReturnMatchingDocuments() {
        // Given a page of matching entities
        Page<Document> docs = new PageImpl<>(List.of(sampleDoc));
        when(documentRepository.searchByKeyword(eq("Test"), anyList(), any(Pageable.class)))
            .thenReturn(docs);

        // When
        Page<DocumentResponseDTO> result = service.search("Test", List.of(2L), Pageable.unpaged());

        // Then
        assertThat(result.getContent()).containsExactly(sampleDTO);
    }

    @Test
    void update_shouldModifyFields_andReturnUpdatedDTO() {
        // Prepare an update payload
        DocumentUpdateDTO updateDTO = new DocumentUpdateDTO();
        updateDTO.setTitleEn("Updated");
        updateDTO.setTitleEs("UpdatedEs");
        updateDTO.setDescription("New Desc");
        updateDTO.setCategoryId(1L);
        updateDTO.setDepartmentId(2L);

        // Prepare what save(...) should return
        Document updatedDoc = new Document();
        updatedDoc.setId(sampleDoc.getId());
        updatedDoc.setTitleEn("Updated");
        updatedDoc.setTitleEs("UpdatedEs");
        updatedDoc.setDescription("New Desc");
        updatedDoc.setFileUrl(sampleDoc.getFileUrl());
        updatedDoc.setFileType(sampleDoc.getFileType());
        updatedDoc.setFileSize(sampleDoc.getFileSize());
        updatedDoc.setCreatedAt(sampleDoc.getCreatedAt());
        updatedDoc.setUpdatedAt(Instant.now());
        updatedDoc.setOwnerId(sampleDoc.getOwnerId());
        updatedDoc.setCategory(sampleDoc.getCategory());
        updatedDoc.setDepartment(sampleDoc.getDepartment());

        // Stub repository and lookup calls
        when(documentRepository.findById(1L)).thenReturn(Optional.of(sampleDoc));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleDoc.getCategory()));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(sampleDoc.getDepartment()));
        when(documentRepository.save(any(Document.class))).thenReturn(updatedDoc);

        // And stub mapper for this specific updatedDoc
        when(documentMapper.toDTO(updatedDoc)).thenReturn(
            DocumentResponseDTO.builder()
                .id(updatedDoc.getId())
                .titleEn(updatedDoc.getTitleEn())
                .titleEs(updatedDoc.getTitleEs())
                .description(updatedDoc.getDescription())
                .fileUrl(updatedDoc.getFileUrl())
                .fileType(updatedDoc.getFileType())
                .fileSize(updatedDoc.getFileSize())
                .createdAt(updatedDoc.getCreatedAt())
                .updatedAt(updatedDoc.getUpdatedAt())
                .ownerId(updatedDoc.getOwnerId())
                .categoryId(updatedDoc.getCategory().getId())
                .categoryName(updatedDoc.getCategory().getName())
                .departmentId(updatedDoc.getDepartment().getId())
                .departmentName(updatedDoc.getDepartment().getName())
                .ownerEmail(null)
                .build()
        );

        // When
        DocumentResponseDTO response = service.update(1L, updateDTO);

        // Then
        assertThat(response.getTitleEn()).isEqualTo("Updated");
        assertThat(response.getDescription()).isEqualTo("New Desc");
        verify(documentRepository).save(any(Document.class));
    }
}
