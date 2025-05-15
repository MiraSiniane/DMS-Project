package com.service.document.service;

import com.service.document.dto.FileUploadResponseDTO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.service.document.exception.ResourceNotFoundException;

public interface FileStorageService {
    FileUploadResponseDTO store(MultipartFile file);
    Resource load(String fileUrl) throws ResourceNotFoundException;
    void delete(String fileUrl);
    String getFileExtension(String contentType);
}