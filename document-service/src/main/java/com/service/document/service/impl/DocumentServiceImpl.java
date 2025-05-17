package com.service.document.service.impl;

import com.service.document.client.AuthServiceClient;
import com.service.document.client.StorageClient;
import com.service.document.client.StorageResponse;
import com.service.document.dto.DocumentCreateDTO;
import com.service.document.dto.DocumentResponseDTO;
import com.service.document.dto.DocumentUpdateDTO;
import com.service.document.dto.UserInfoDTO;
import com.service.document.entity.Category;
import com.service.document.entity.Document;
import com.service.document.event.DocumentCreatedEvent;
import com.service.document.exception.ForbiddenAccessException;
import com.service.document.exception.ResourceNotFoundException;
import com.service.document.mapper.DocumentMapper;
import com.service.document.repository.CategoryRepository;
import com.service.document.repository.DocumentRepository;
import com.service.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CategoryRepository categoryRepository;
    private final StorageClient storageClient;
    private final AuthServiceClient authServiceClient;
    private final KafkaTemplate<String, DocumentCreatedEvent> kafkaTemplate;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentResponseDTO createDocument(DocumentCreateDTO dto,
                                              MultipartFile file,
                                              String authHeader) {
        UserInfoDTO user = authServiceClient.getCurrentUser(authHeader);
        Long deptId = dto.departmentId();
        if (!authServiceClient.hasDepartmentAccess(authHeader, deptId)) {
            throw new ForbiddenAccessException("No access to department " + deptId);
        }

        StorageResponse sr = storageClient.upload(file, authHeader);
        Category cat = categoryRepository.findById(dto.categoryId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Category not found: " + dto.categoryId()));

        Document doc = Document.builder()
            .title(dto.title())
            .translatedTitle(dto.translatedTitle())
            .s3Key(sr.getKey())
            .contentType(file.getContentType())
            .fileSize(file.getSize())
            .createdAt(Instant.now())
            .userId(Long.parseLong(user.id()))
            .departmentId(deptId)
            .category(cat)
            .build();

        Document saved = documentRepository.save(doc);
        kafkaTemplate.send("document-events",
            new DocumentCreatedEvent());
        return documentMapper.toResponseDTO(saved, user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> getDocumentsByUserDepartments(String authHeader,
                                                                   Pageable pageable) {
        Collection<Long> depts = authServiceClient.getUserDepartments(authHeader);
        return documentRepository.findByDepartmentIdIn(depts, pageable)
            .map(doc -> {
                UserInfoDTO owner = authServiceClient.getUserById(authHeader, doc.getUserId());
                return documentMapper.toResponseDTO(doc, owner);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentResponseDTO getDocumentById(Long id, String authHeader) {
        Document doc = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        if (!authServiceClient.hasDepartmentAccess(authHeader, doc.getDepartmentId())) {
            throw new ForbiddenAccessException("No access to document " + id);
        }
        UserInfoDTO owner = authServiceClient.getUserById(authHeader, doc.getUserId());
        return documentMapper.toResponseDTO(doc, owner);
    }

    @Override
    @Transactional
    public DocumentResponseDTO updateDocumentMetadata(Long id,
                                                      DocumentUpdateDTO dto,
                                                      String authHeader) {
        Document doc = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        if (!authServiceClient.hasDepartmentAccess(authHeader, doc.getDepartmentId())) {
            throw new ForbiddenAccessException("No access to document " + id);
        }

        if (dto.title() != null) doc.setTitle(dto.title());
        if (dto.translatedTitle() != null) doc.setTranslatedTitle(dto.translatedTitle());
        if (dto.categoryId() != null) {
            Category cat = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Category not found: " + dto.categoryId()));
            doc.setCategory(cat);
        }
        if (dto.departmentId() != null) {
            Long newDept = dto.departmentId();
            if (!authServiceClient.hasDepartmentAccess(authHeader, newDept)) {
                throw new ForbiddenAccessException("No access to department " + newDept);
            }
            doc.setDepartmentId(newDept);
        }

        Document updated = documentRepository.save(doc);
        UserInfoDTO owner = authServiceClient.getUserById(authHeader, doc.getUserId());
        return documentMapper.toResponseDTO(updated, owner);
    }

    @Override
    @Transactional
    public DocumentResponseDTO updateDocumentWithFile(Long id,
                                                  DocumentUpdateDTO dto,
                                                  MultipartFile file,
                                                  String authHeader) {
        Document doc = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        if (!authServiceClient.hasDepartmentAccess(authHeader, doc.getDepartmentId())) {
            throw new ForbiddenAccessException("No access to document " + id);
        }

        // Upload new file
        StorageResponse sr = storageClient.upload(file, authHeader);
        doc.setS3Key(sr.getKey());
        doc.setContentType(file.getContentType());
        doc.setFileSize(file.getSize());

        // Update metadata from DocumentUpdateDTO
        if (dto.title() != null) {
            doc.setTitle(dto.title());
        }
        if (dto.translatedTitle() != null) {
            doc.setTranslatedTitle(dto.translatedTitle());
        }
        if (dto.categoryId() != null) {
            Category cat = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Category not found: " + dto.categoryId()));
            doc.setCategory(cat);
        }
        if (dto.departmentId() != null) {
            Long newDept = dto.departmentId();
            if (!authServiceClient.hasDepartmentAccess(authHeader, newDept)) {
                throw new ForbiddenAccessException("No access to department " + newDept);
            }
            doc.setDepartmentId(newDept);
        }

        Document updated = documentRepository.save(doc);
        UserInfoDTO owner = authServiceClient.getUserById(authHeader, updated.getUserId());
        return documentMapper.toResponseDTO(updated, owner);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id, String authHeader) {
        Document doc = documentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
        if (!authServiceClient.hasDepartmentAccess(authHeader, doc.getDepartmentId())) {
            throw new ForbiddenAccessException("No access to document " + id);
        }

        storageClient.deleteFile(doc.getS3Key(), authHeader);
        documentRepository.delete(doc);

        UserInfoDTO user = authServiceClient.getCurrentUser(authHeader);
        kafkaTemplate.send("document-events",
            new DocumentCreatedEvent());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> searchDocuments(String keyword,
                                                     String authHeader,
                                                     Pageable pageable) {
        Collection<Long> depts = authServiceClient.getUserDepartments(authHeader);
        return documentRepository
            .searchByKeywordAndDepartmentIds(keyword, depts, pageable)
            .map(doc -> {
                UserInfoDTO owner = authServiceClient.getUserById(authHeader, doc.getUserId());
                return documentMapper.toResponseDTO(doc, owner);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponseDTO> getDepartmentDocuments(Long departmentId,
                                                            String authHeader,
                                                            Pageable pageable) {
        if (!authServiceClient.hasDepartmentAccess(authHeader, departmentId)) {
            throw new ForbiddenAccessException("No access to department " + departmentId);
        }
        return documentRepository
            .findByDepartmentIdIn(Collections.singletonList(departmentId), pageable)
            .map(doc -> {
                UserInfoDTO owner = authServiceClient.getUserById(authHeader, doc.getUserId());
                return documentMapper.toResponseDTO(doc, owner);
            });
    }
}