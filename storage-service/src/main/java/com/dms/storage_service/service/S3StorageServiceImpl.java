package com.dms.storage_service.service;

import com.dms.storage_service.exception.FileNotFoundException;
import com.dms.storage_service.exception.FileStorageException;
import com.dms.storage_service.model.FileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class S3StorageServiceImpl implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    @Autowired
    public S3StorageServiceImpl(S3Client s3Client, 
                             S3Presigner s3Presigner,
                             @Value("${aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    @Override
    public FileResponse uploadFile(MultipartFile file, String department) {
        try {
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileId = UUID.randomUUID().toString();
            String key = generateS3Key(department, fileId, originalFileName);

            // Store filename in metadata (lowercase for MinIO compatibility)
            Map<String, String> metadata = new HashMap<>();
            metadata.put("fileid", fileId);
            metadata.put("filename", originalFileName); // <-- Critical
            metadata.put("department", department);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .metadata(metadata)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return FileResponse.builder()
                    .fileName(originalFileName)
                    .fileType(file.getContentType())
                    .fileSize(formatFileSize(file.getSize()))
                    .fileId(fileId)
                    .build();
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file", ex);
        }
    }

    @Override
    public FileResponse getPreSignedUrlForUpload(String fileName, String contentType, String department) {
        try {
            String fileId = UUID.randomUUID().toString();
            String key = generateS3Key(department, fileId, fileName);

            // Create a map for signed headers
            Map<String, String> metadata = new HashMap<>();
            metadata.put("fileId", fileId);
            metadata.put("fileName", fileName);
            metadata.put("department", department);

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            // Replace internal endpoint with external one
            String url = presignedRequest.url().toString();
            url = url.replace("http://minio:9000", "http://localhost:9000");

            return FileResponse.builder()
                    .fileName(fileName)
                    .fileType(contentType)
                    .uploadUrl(url)
                    .fileId(fileId)
                    .expiryTime(15 * 60L)
                    .build();
        } catch (Exception ex) {
            throw new FileStorageException("Failed to generate pre-signed URL for upload", ex);
        }
    }


   
    @Override
    public FileResponse getPreSignedUrlForDownload(String fileId) {
        try {
            String key = findKeyByFileId(fileId);
            
            // Get object metadata first
            HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            
            Map<String, String> metadataMap = metadata.metadata();
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String downloadUrl = presignedRequest.url().toString()
                    .replace("http://minio:9000", "http://localhost:9000");
            
            return FileResponse.builder()
                    .fileName(metadataMap != null ? metadataMap.get("filename") : 
                            key.substring(key.lastIndexOf('/') + 1)) // Fallback
                    .fileType(metadata.contentType())
                    .fileSize(formatFileSize(metadata.contentLength()))
                    .downloadUrl(downloadUrl)
                    .fileId(fileId)
                    .expiryTime(15 * 60L)
                    .build();
        } catch (NoSuchKeyException e) {
            throw new FileNotFoundException("File not found with id: " + fileId);
        } catch (Exception e) {
            log.error("Error generating download URL for {}: {}", fileId, e.getMessage());
            throw new FileStorageException("Failed to generate download URL", e);
        }
    }


    @Override
    public void deleteFile(String fileId) {
        try {
            String key = findKeyByFileId(fileId);
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteRequest);
        } catch (NoSuchKeyException ex) {
            throw new FileNotFoundException("File not found with id: " + fileId);
        } catch (Exception ex) {
            throw new FileStorageException("Failed to delete file", ex);
        }
    }

 
    @Override
    public List<FileResponse> listFiles(String department) {
        try {
            String prefix = department + "/";
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            List<FileResponse> fileResponses = new ArrayList<>();

            for (S3Object s3Object : listResponse.contents()) {
                try {
                    HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Object.key())
                            .build());

                    // Handle metadata (note lowercase keys)
                    Map<String, String> metadataMap = metadata.metadata();
                    String fileId = metadataMap.get("fileid");
                    String fileName = metadataMap.get("filename");
                    
                    // Fallback to extract from key if metadata missing
                    if (fileName == null) {
                        fileName = s3Object.key().substring(s3Object.key().lastIndexOf('/') + 1);
                    }

                    // Generate download URL
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Object.key())
                            .build();

                    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(15))
                            .getObjectRequest(getObjectRequest)
                            .build();

                    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
                    String downloadUrl = presignedRequest.url().toString()
                            .replace("http://minio:9000", "http://localhost:9000");

                    fileResponses.add(FileResponse.builder()
                            .fileName(fileName)
                            .fileType(metadata.contentType())
                            .fileSize(formatFileSize(metadata.contentLength()))
                            .fileId(fileId)
                            .downloadUrl(downloadUrl)
                            .build());
                } catch (Exception e) {
                    log.error("Error processing file {}: {}", s3Object.key(), e.getMessage());
                }
            }
            return fileResponses;
        } catch (Exception ex) {
            throw new FileStorageException("Failed to list files", ex);
        }
    }

   

    @Override
    public byte[] downloadFile(String fileId) {
        try {
            String key = findKeyByFileId(fileId);
            
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException ex) {
            throw new FileNotFoundException("File not found with id: " + fileId);
        } catch (Exception ex) {
            throw new FileStorageException("Failed to download file", ex);
        }
    }

    private String findKeyByFileId(String fileId) {
        try {
            // List all objects in the bucket
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            
            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
            
            for (S3Object s3Object : listResponse.contents()) {
                try {
                    // Get object metadata
                    HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Object.key())
                            .build());
                    
                    // Check metadata (note lowercase keys for MinIO)
                    Map<String, String> metadataMap = metadata.metadata();
                    String metadataFileId = metadataMap != null ? 
                            metadataMap.get("fileid") : // MinIO stores metadata keys in lowercase
                            null;
                    
                    if (fileId.equals(metadataFileId)) {
                        return s3Object.key();
                    }
                } catch (Exception e) {
                    log.warn("Error checking metadata for {}: {}", s3Object.key(), e.getMessage());
                }
            }
            throw new FileNotFoundException("File not found with id: " + fileId);
        } catch (S3Exception e) {
            throw new FileStorageException("Error searching for file", e);
        }
    }

    private String generateS3Key(String department, String fileId, String fileName) {
        return department + "/" + fileId + "/" + fileName;
    }

    private String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}