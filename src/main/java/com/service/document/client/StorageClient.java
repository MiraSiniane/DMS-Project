package com.service.document.client;

import com.service.document.dto.FileUploadResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "storage-service", url = "${app.services.storage.url}")
public interface StorageClient {
    
    @PostMapping(value = "/api/storage/upload", consumes = "multipart/form-data")
    FileUploadResponseDTO upload(@RequestPart("file") MultipartFile file);
    
    @PostMapping("/api/storage")
    FileUploadResponseDTO uploadFile(@RequestPart("file") MultipartFile file);
    
    @GetMapping("/api/storage/url/{key}")
    String getPresignedUrl(@PathVariable("key") String key);
    
    @DeleteMapping("/api/storage/{key}")
    void deleteFile(@PathVariable("key") String key);
}
