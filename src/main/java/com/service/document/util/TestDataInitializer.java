package com.service.document.util;

import com.service.document.dto.CategoryCreateDTO;
import com.service.document.entity.Department;
import com.service.document.repository.DepartmentRepository;
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
    private final DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) {
        initDepartments();
        initCategories();
    }

    private void initDepartments() {
        saveDepartment(1L, "IT Department");
        saveDepartment(2L, "HR Department");
        saveDepartment(3L, "Finance Department");
    }

    private void saveDepartment(Long id, String name) {
        if (departmentRepository.findById(id).isEmpty()) {
            Department dept = new Department();
            dept.setId(id);
            dept.setName(name);
            departmentRepository.save(dept);
        }
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
