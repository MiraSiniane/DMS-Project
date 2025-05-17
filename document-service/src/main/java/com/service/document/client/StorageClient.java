// StorageClient.java
package com.service.document.client;

import java.time.Instant;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import com.service.document.client.StorageClientFallback;

/**
 * Feign client for interacting with the storage-service.
 * The URL placeholder now uses the correctly-resolved property key and falls back to the service name.
 */
@FeignClient(
    name = "storage-service", 
    url = "${app.services.storage.url:http://storage-service:8083}", 
    fallback = StorageClientFallback.class
)
public interface StorageClient {

    @PostMapping(
        value = "/api/storage", 
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    StorageResponse upload(
        @RequestPart("file") org.springframework.web.multipart.MultipartFile file,
        @RequestHeader("Authorization") String authHeader
    );

    @GetMapping(
        value = "/api/storage/download/{fileId}",
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    ResponseEntity<Resource> download(
        @PathVariable("fileId") String fileId,
        @RequestHeader("Authorization") String authHeader
    );

    @GetMapping(
        value = "/api/storage/{fileId}/metadata",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    StorageResponse getMetadata(
        @PathVariable("fileId") String fileId,
        @RequestHeader("Authorization") String authHeader
    );

    @GetMapping(
        value = "/api/storage/list",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    Page<StorageResponse> listFiles(
        @RequestParam("department") String department,
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size
    );

    @GetMapping(
        value = "/api/storage/search",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    Page<StorageResponse> searchFiles(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "startDate", required = false) Instant startDate,
        @RequestParam(value = "endDate", required = false) Instant endDate,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestHeader("Authorization") String authHeader
    );

    @DeleteMapping(
        value = "/api/storage/{fileId}"
    )
    ResponseEntity<Void> deleteFile(
        @PathVariable("fileId") String fileId,
        @RequestHeader("Authorization") String authHeader
    );

    @DeleteMapping(
        value = "/api/storage/batch"
    )
    ResponseEntity<Void> deleteFiles(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody List<String> fileIds
    );

    @GetMapping(
    value = "/api/storage/generate-download-url/{fileId}",
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    StorageResponse getFile(
    @PathVariable("fileId") String fileId,
    @RequestHeader("Authorization") String authHeader
);

}