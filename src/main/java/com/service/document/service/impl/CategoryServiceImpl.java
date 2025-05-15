package com.service.document.service.impl;

import com.service.document.client.StorageClient;
import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentUpdateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.entity.Category;
import com.service.document.entity.Department;
import com.service.document.entity.Document;
import com.service.document.event.DocumentCreatedEvent;
import com.service.document.mapper.DocumentMapper;
import com.service.document.repository.CategoryRepository;
import com.service.document.repository.DepartmentRepository;
import com.service.document.repository.DocumentRepository;
import com.service.document.service.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;
    private final StorageClient storageClient;
    private final KafkaTemplate<String, DocumentCreatedEvent> kafkaTemplate;
    private final DocumentMapper documentMapper;

    public DocumentServiceImpl(
        DocumentRepository documentRepository,
        CategoryRepository categoryRepository,
        DepartmentRepository departmentRepository,
        StorageClient storageClient,
        KafkaTemplate<String, DocumentCreatedEvent> kafkaTemplate,
        DocumentMapper documentMapper
    ) {
        this.documentRepository = documentRepository;
        this.categoryRepository = categoryRepository;
        this.departmentRepository = departmentRepository;
        this.storageClient = storageClient;
        this.kafkaTemplate = kafkaTemplate;
        this.documentMapper = documentMapper;
    }

    @Override
    public DocumentResponseDTO create(
        DocumentCreateDTO createDTO,
        MultipartFile file,
        Long ownerId,
        Collection<Long> departmentIds
    ) {
        // 1. Upload file
        String fileUrl = storageClient.upload(file);

        // 2. Resolve category & department
        Category category = categoryRepository.findById(createDTO.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + createDTO.getCategoryId()));
        Department department = departmentRepository.findById(createDTO.getDepartmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + createDTO.getDepartmentId()));

        // 3. Build new Document
        Document document = new Document();
        document.setTitleEn(createDTO.getTitleEn());
        document.setTitleEs(createDTO.getTitleEs());
        document.setDescription(createDTO.getDescription());
        document.setFileUrl(fileUrl);
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setCreatedAt(Instant.now());
        document.setUpdatedAt(Instant.now());
        document.setOwnerId(ownerId);
        document.setCategory(category);
        document.setDepartment(department);

        // 4. Save & publish
        Document saved = documentRepository.save(document);
        kafkaTemplate.send("document-events", new DocumentCreatedEvent(saved.getId(), ownerId, Instant.now()));

        // 5. Map to DTO
        return documentMapper.toDTO(saved);
    }

    @Override
    public DocumentResponseDTO update(
        Long id,
        DocumentCreateDTO updateDTO,
        MultipartFile file,
        Long ownerId,
        Collection<Long> departmentIds
    ) {
        // 1. Fetch
        Document existing = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found: " + id));

        // 2. Optional file update
        if (file != null && !file.isEmpty()) {
            String fileUrl = storageClient.upload(file);
            existing.setFileUrl(fileUrl);
            existing.setFileType(file.getContentType());
            existing.setFileSize(file.getSize());
        }

        // 3. Update metadata
        existing.setTitleEn(updateDTO.getTitleEn());
        existing.setTitleEs(updateDTO.getTitleEs());
        existing.setDescription(updateDTO.getDescription());
        existing.setOwnerId(ownerId);

        Category category = categoryRepository.findById(updateDTO.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + updateDTO.getCategoryId()));
        existing.setCategory(category);

        Department department = departmentRepository.findById(updateDTO.getDepartmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + updateDTO.getDepartmentId()));
        existing.setDepartment(department);

        existing.setUpdatedAt(Instant.now());

        // 4. Save & publish
        Document saved = documentRepository.save(existing);
        kafkaTemplate.send("document-events", new DocumentCreatedEvent(saved.getId(), ownerId, Instant.now()));

        // 5. Map
        return documentMapper.toDTO(saved);
    }

    @Override
    public DocumentResponseDTO update(Long id, DocumentUpdateDTO dto) {
        Document existing = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found: " + id));

        existing.setTitleEn(dto.getTitleEn());
        existing.setTitleEs(dto.getTitleEs());
        existing.setDescription(dto.getDescription());

        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.getCategoryId()));
        existing.setCategory(category);

        Department department = departmentRepository.findById(dto.getDepartmentId())
            .orElseThrow(() -> new EntityNotFoundException("Department not found: " + dto.getDepartmentId()));
        existing.setDepartment(department);

        existing.setUpdatedAt(Instant.now());

        Document saved = documentRepository.save(existing);
        return documentMapper.toDTO(saved);
    }

    @Override
    public void delete(
        Long id,
        Long userId,
        Collection<Long> departmentIds
    ) {
        Document existing = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found: " + id));

        documentRepository.delete(existing);
        kafkaTemplate.send("document-events", new DocumentCreatedEvent(id, userId, Instant.now()));
    }

    @Override
    public Page<DocumentResponseDTO> search(
        String keyword,
        Collection<Long> departmentIds,
        Pageable pageable
    ) {
        Page<Document> page = documentRepository.searchByKeyword(keyword, departmentIds, pageable);
        return page.map(documentMapper::toDTO);
    }

    @Override
    public DocumentResponseDTO getDocumentById(Long id) {
        Document existing = documentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Document not found: " + id));
        return documentMapper.toDTO(existing);
    }
}
