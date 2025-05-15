package com.service.document.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class PathUtils {
    private static final Logger logger = LoggerFactory.getLogger(PathUtils.class);
    
    /**
     * Safely converts a string to a Path, handling exceptions
     * @param pathStr String representation of a path
     * @return Optional containing valid Path or empty if invalid
     */
    public static Optional<Path> safePath(String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            // Additional validation could be added here
            return Optional.of(path);
        } catch (InvalidPathException | NullPointerException e) {
            logger.error("Invalid path: {}", pathStr, e);
            return Optional.empty();
        }
    }
}