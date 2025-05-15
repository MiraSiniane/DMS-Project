package com.service.document.service.impl;

import com.service.document.client.StorageClient;
import com.service.document.dto.FileUploadResponseDTO;
import com.service.document.exception.ResourceNotFoundException;
import com.service.document.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.net.MalformedURLException;

@Service
@Primary
@RequiredArgsConstructor
public class FeignFileStorageService implements FileStorageService {

    private final StorageClient storageClient;

    @Override
    public FileUploadResponseDTO store(MultipartFile file) {
        return storageClient.upload(file);

    }

    @Override
    public Resource load(String fileUrl) throws ResourceNotFoundException {
        try {
            // Assume fileUrl is the storage key
            String presigned = storageClient.getPresignedUrl(fileUrl);
            UrlResource resource = new UrlResource(presigned);
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Could not read file: " + fileUrl);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Malformed URL for file: " + fileUrl);
        }
    }

    @Override
    public void delete(String fileUrl) {
        storageClient.deleteFile(fileUrl);
    }

    @Override
    public String getFileExtension(String contentType) {
        if (contentType == null) {
            return "";
        }
        int idx = contentType.lastIndexOf('.');
        return (idx < 0 || idx == contentType.length() - 1) 
            ? "" 
            : contentType.substring(idx + 1);
    }
}
