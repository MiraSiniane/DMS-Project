package com.service.document.controller;

import com.service.document.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FileSearchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SearchService searchService;

    @InjectMocks
    private FileSearchController fileSearchController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(fileSearchController).build();
    }

    @Test
    void searchFiles_returnsMockedPaths() throws Exception {
        // Arrange
        when(searchService.findFilesIn(any(Path.class)))
            .thenReturn(List.of(
                Path.of("/valid/path/file1.txt"),
                Path.of("/valid/path/file2.pdf")
            ));

        // Determine expected path format based on OS
        String separator = System.getProperty("file.separator");
        String expectedPath1 = separator.equals("\\") ? 
            "\\valid\\path\\file1.txt" : "/valid/path/file1.txt";
        String expectedPath2 = separator.equals("\\") ? 
            "\\valid\\path\\file2.pdf" : "/valid/path/file2.pdf";
            
        // Act & Assert
        mockMvc.perform(get("/api/files/search")
                .param("root", "/valid/path")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0]").value(expectedPath1))
            .andExpect(jsonPath("$[1]").value(expectedPath2));
    }
}