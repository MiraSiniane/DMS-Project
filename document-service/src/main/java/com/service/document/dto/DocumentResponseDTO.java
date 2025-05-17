package com.service.document.dto;


import lombok.Builder;
import lombok.Data;


import java.time.Instant;

import com.service.document.entity.Document.DocumentBuilder;

@Data

public class DocumentResponseDTO {
    private Long    id;
    private String  title;
    private String  translatedTitle;
    private String  fileUrl;       // ← map from s3Key / presigned URL
    private String  fileType;      // ← map from contentType
    private Long    fileSize;
    private Instant createdAt;
    private Long    categoryId;
    private Long    departmentId;
    private String  owner;         // ← full name
    private Long    ownerId;
}
