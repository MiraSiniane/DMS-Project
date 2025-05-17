package com.service.document.service.impl;

import com.service.document.service.SearchService;
import com.service.document.util.PathUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileSystemSearchService implements SearchService {
    
    @Override
    public List<Path> findFilesIn(Path root) {
        // Call the extended method with no filtering (null = all files)
        return findFilesIn(root, null);
    }

    /**
     * Extended method to filter files by extensions (e.g., "txt", "pdf").
     * If extensions is null or empty, returns all files.
     */
    public List<Path> findFilesIn(Path root, Set<String> extensions) {
        Optional<Path> maybeRoot = PathUtils.safePath(root.toString());
        if (maybeRoot.isEmpty()) {
            return List.of();
        }
        Path validRoot = maybeRoot.get();
        try (Stream<Path> stream = Files.walk(validRoot)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(path -> extensions == null || extensions.isEmpty() || 
                    hasExtension(path, extensions))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search files under " + validRoot, e);
        }
    }

    // Helper to check if the file has one of the target extensions (case-insensitive)
    private boolean hasExtension(Path path, Set<String> extensions) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return false; // No extension
        
        String fileExtension = fileName.substring(dotIndex + 1).toLowerCase();
        return extensions.stream()
            .map(String::toLowerCase)
            .anyMatch(fileExtension::equals);
    }
}