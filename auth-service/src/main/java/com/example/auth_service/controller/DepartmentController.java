package com.example.auth_service.controller;

import com.example.auth_service.dto.DepartmentAssignmentDTO;
import com.example.auth_service.dto.DepartmentListDTO;
import com.example.auth_service.model.Department;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.DepartmentRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.DepartmentService;
import com.example.auth_service.util.RoleUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    // Create department (SuperAdmin only)
    @PostMapping
    public ResponseEntity<Department> createDepartment(
            @RequestBody String departmentName,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        // Create department
        Department department = new Department();
        department.setName(departmentName.trim());
        Department savedDepartment = departmentRepository.save(department);
        
        // Assign to SuperAdmin
        User superAdmin = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SuperAdmin not found"));
        
        superAdmin.getDepartments().add(savedDepartment);
        userRepository.save(superAdmin);
        
        return ResponseEntity.ok(savedDepartment);
    }

    // Delete department (SuperAdmin only)
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long departmentId,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        departmentRepository.deleteById(departmentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<User> assignDepartment(
        @RequestBody DepartmentAssignmentDTO assignment,
        @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = userRepository.findById(assignment.getUserId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Department department = departmentRepository.findById(assignment.getDepartmentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        boolean isSuperAdmin = RoleUtils.isSuperAdmin(currentUser);
        boolean isAdmin = RoleUtils.isAdmin(currentUser);
        boolean targetIsUser = user.getRole().getName().equals(Role.RoleType.USER);
        
        if (!isSuperAdmin && !(isAdmin && targetIsUser)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Permission denied. Admins can only assign to regular users.");
        }
        
        user.getDepartments().add(department);
        return ResponseEntity.ok(userRepository.save(user));
   }

    // Remove department from user
    @PostMapping("/unassign")
    public ResponseEntity<User> unassignDepartment(
            @RequestBody DepartmentAssignmentDTO assignment,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = userRepository.findById(assignment.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        Department department = departmentRepository.findById(assignment.getDepartmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        // Check permissions
        boolean isSuperAdmin = RoleUtils.isSuperAdmin(currentUser);
        boolean isAdmin = RoleUtils.isAdmin(currentUser);
        boolean targetIsUser = user.getRole().getName().equals(Role.RoleType.USER); // Using String comparison
        if (!isSuperAdmin && !(isAdmin && targetIsUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        user.getDepartments().remove(department);
        return ResponseEntity.ok(userRepository.save(user));
    }
    
    @GetMapping("/getalldepartments")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return ResponseEntity.ok(departments);
    }


    // Add to DepartmentController.java
    private final DepartmentService departmentService;
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Page<DepartmentListDTO>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(departmentService.getAllDepartments(pageable));
    }
}

