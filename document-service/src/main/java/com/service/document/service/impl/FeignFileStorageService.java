package com.service.document.service.impl;

import com.service.document.client.StorageClient;
import com.service.document.client.StorageResponse;
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
    public StorageResponse store(MultipartFile file) {
        return storageClient.upload(file, null);
    }

    @Override
    public Resource load(String key) {
        try {
            StorageResponse resp = storageClient.getFile(key, null);
            UrlResource resource = new UrlResource(resp.getPresignedUrl());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Could not read file: " + key);
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Malformed URL for file: " + key);
        }
    }

    @Override
    public void delete(String key) {
        storageClient.deleteFile(key, null);
    }

    @Override
    public String getFileExtension(String contentType) {
        if (contentType == null) return "";
        int slash = contentType.indexOf('/');
        return (slash < 0 || slash == contentType.length() - 1)
            ? "" : contentType.substring(slash + 1);
    }
}
