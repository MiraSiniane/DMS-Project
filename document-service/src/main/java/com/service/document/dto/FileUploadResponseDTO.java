package com.service.document.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FileUploadResponseDTO {
    private String filename;
    private String fileType;
    private Long size;
    private String downloadUrl;
    private String url;
}