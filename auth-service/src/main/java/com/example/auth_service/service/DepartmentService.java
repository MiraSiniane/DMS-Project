// src/main/java/com/example/auth_service/service/DepartmentService.java
package com.example.auth_service.service;

import com.example.auth_service.dto.DepartmentListDTO;
import com.example.auth_service.model.Department;
import com.example.auth_service.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public Page<DepartmentListDTO> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAllWithUserCount(pageable)
            .map(result -> {
                Department department = (Department) result[0];
                Long userCount = (Long) result[1];
                
                DepartmentListDTO dto = new DepartmentListDTO();
                dto.setId(department.getId());
                dto.setName(department.getName());
                dto.setCreatedAt(department.getCreatedAt());
                dto.setUserCount(userCount.intValue());
                dto.setDocumentCount(0); // Will implement later
                return dto;
            });
    }
}