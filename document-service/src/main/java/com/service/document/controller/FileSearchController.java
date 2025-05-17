package com.service.document.controller;

import com.service.document.service.SearchService;
import com.service.document.util.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileSearchController {

    private final SearchService searchService;

    @Autowired
    public FileSearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchFiles(@RequestParam String root) {
        Optional<Path> rootPath = PathUtils.safePath(root);
        
        if (rootPath.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
        
        List<Path> files = searchService.findFilesIn(rootPath.get());
        
        List<String> paths = files.stream()
            .map(Path::toString)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(paths);
    }
}