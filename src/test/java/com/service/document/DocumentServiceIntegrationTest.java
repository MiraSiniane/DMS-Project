package com.service.document;

import com.service.document.service.SearchService;
import com.service.document.service.impl.FileSystemSearchService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


 @SpringBootTest(
   classes = FileSystemSearchService.class,
   webEnvironment = WebEnvironment.NONE
 )
class DocumentServiceIntegrationTest {

    @Autowired
    private SearchService searchService;

    @TempDir
    static Path tempDir;

    @BeforeAll
    static void init() throws Exception {
        // Create a single sample file in the temp directory
        Files.writeString(tempDir.resolve("sample.txt"), "hello");
    }

    @Test
    void findFilesIn_tempDir_returnsSampleFile() {
        List<Path> results = searchService.findFilesIn(tempDir);
        // Assert that we only see our sample file
        assertThat(results)
            .map(Path::getFileName)
            .contains(Path.of("sample.txt"));
    }
}
