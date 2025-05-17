// StorageClientFallback.java
package com.service.document.client;

import java.time.Instant;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.service.document.client.StorageClient;
import com.service.document.client.StorageResponse;

@Component
public class StorageClientFallback implements StorageClient {

    private static final String UNAVAILABLE = "Storage service is unavailable";

    @Override
    public StorageResponse upload(org.springframework.web.multipart.MultipartFile file, String authHeader) {
        throw new RuntimeException(UNAVAILABLE);
    }

    @Override
    public ResponseEntity<Resource> download(String fileId, String authHeader) {
        return ResponseEntity.status(503).build();
    }

    @Override
    public StorageResponse getMetadata(String fileId, String authHeader) {
        throw new RuntimeException(UNAVAILABLE);
    }

    @Override
    public Page<StorageResponse> listFiles(String department, String authHeader, int page, int size) {
        return Page.empty();
    }

    @Override
    public Page<StorageResponse> searchFiles(String keyword, Instant startDate, Instant endDate, int page, int size, String authHeader) {
        return Page.empty();
    }

    @Override
    public ResponseEntity<Void> deleteFile(String fileId, String authHeader) {
        return ResponseEntity.status(503).build();
    }

    @Override
    public ResponseEntity<Void> deleteFiles(String authHeader, List<String> fileIds) {
        return ResponseEntity.status(503).build();
    }
    @Override
    public StorageResponse getFile(String fileId, String authHeader) {
        throw new RuntimeException(UNAVAILABLE);
    }

    
}
