package com.example.auth_service.controller;

import com.example.auth_service.dto.DepartmentAssignmentDTO;
import com.example.auth_service.model.Department;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.DepartmentRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.RoleUtils;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        
        // Debug: Print user's role
        System.out.println("Target user role: " + user.getRole().getName());
        
        // Check permissions
        boolean isSuperAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_SUPERADMIN"));
        
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        boolean targetIsUser = user.getRole().getName().toString().trim().equalsIgnoreCase("USER");

        if (!isSuperAdmin && !(isAdmin && targetIsUser)) {
                        System.out.println(isSuperAdmin);
                        System.out.println(isAdmin);
                        System.out.println(targetIsUser);
                        System.out.println(user.getRole().getName());

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
        boolean targetIsUser = user.getRole().getName().equals("USER"); // Using String comparison
        
        if (!isSuperAdmin && !(isAdmin && targetIsUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        user.getDepartments().remove(department);
        return ResponseEntity.ok(userRepository.save(user));
    }
}