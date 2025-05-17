package com.dms.storage_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {
    private String fileName;
    private String fileType;
    private String fileSize;
    private String downloadUrl;
    private String uploadUrl;
    private String fileId;
    private String previewUrl;
    private Long expiryTime;
}