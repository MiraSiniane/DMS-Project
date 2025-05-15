package com.dms.storage_service.controller;

import com.dms.storage_service.model.FileResponse;
import com.dms.storage_service.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
public class FileController {

    private final StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("department") String department) {
        
        FileResponse response = storageService.uploadFile(file, department);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/generate-upload-url")
    public ResponseEntity<FileResponse> generatePreSignedUploadUrl(
            @RequestParam("fileName") String fileName,
            @RequestParam("contentType") String contentType,
            @RequestParam("department") String department) {
        
        FileResponse response = storageService.getPreSignedUrlForUpload(fileName, contentType, department);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        byte[] fileContent = storageService.downloadFile(fileId);
        FileResponse fileResponse = storageService.getPreSignedUrlForDownload(fileId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileResponse.getFileName());
        headers.setContentType(MediaType.parseMediaType(fileResponse.getFileType()));
        
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping("/generate-download-url/{fileId}")
    public ResponseEntity<FileResponse> generatePreSignedDownloadUrl(@PathVariable String fileId) {
        FileResponse response = storageService.getPreSignedUrlForDownload(fileId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) {
        storageService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileResponse>> listFiles(@RequestParam("department") String department) {
        List<FileResponse> files = storageService.listFiles(department);
        return ResponseEntity.ok(files);
    }
}