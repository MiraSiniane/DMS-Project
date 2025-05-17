package com.service.document.util;

import com.service.document.dto.CategoryCreateDTO;

import com.service.document.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
   

    @Override
    public void run(String... args) {
       
        initCategories();
    }




    private void initCategories() {
        createCategory("Finance", "Financial documents and reports");
        createCategory("HR", "Human resources documents");
        createCategory("Marketing", "Marketing materials and assets");
    }

    private void createCategory(String name, String description) {
        CategoryCreateDTO dto = new CategoryCreateDTO();
        dto.setName(name);
        dto.setDescription(description);
        categoryService.create(dto, 1L); // Mock user ID for dev
    }
}
