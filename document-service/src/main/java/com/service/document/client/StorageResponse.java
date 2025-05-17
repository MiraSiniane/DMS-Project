package com.service.document.client;

// Response class for storage operations
public class StorageResponse {
    private String key;
    private String fileName;
    private String contentType;
    private String fileSize;
    private String presignedUrl;
    private String downloadUrl;
    
    // Getters and setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getFileSize() { return fileSize; }
    public void setFileSize(String fileSize) { this.fileSize = fileSize; }
    
    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String presignedUrl) { this.presignedUrl = presignedUrl; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public Object getStorageId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getStorageId'");
    }
}