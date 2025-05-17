package com.dms.storage_service.service;

import com.dms.storage_service.model.FileResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StorageService {
    FileResponse uploadFile(MultipartFile file, String department);
    FileResponse getPreSignedUrlForUpload(String fileName, String contentType, String department);
    FileResponse getPreSignedUrlForDownload(String fileId);
    void deleteFile(String fileId);
    List<FileResponse> listFiles(String department);
    byte[] downloadFile(String fileId);
}