package com.service.document.service;

import java.nio.file.Path;
import java.util.List;

/**
 * Service for searching files in a directory
 */
public interface SearchService {
    
    /**
     * Find files in a directory
     * 
     * @param rootPath The root directory to search in
     * @return A list of file paths found
     */
    List<Path> findFilesIn(Path rootPath);
}