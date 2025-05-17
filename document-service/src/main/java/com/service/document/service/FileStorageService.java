package com.service.document.service;

import com.service.document.client.StorageResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * Store a file and return S3 key and presigned URL.
     */
    StorageResponse store(MultipartFile file);

    /**
     * Load a file as a Resource given its storage key.
     */
    Resource load(String key);

    /**
     * Delete a file by its storage key.
     */
    void delete(String key);

    /**
     * Get file extension from content type.
     */
    String getFileExtension(String contentType);
}
