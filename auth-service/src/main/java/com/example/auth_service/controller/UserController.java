package com.example.auth_service.controller;

import com.example.auth_service.dto.DepartmentAssignmentDTO;
import com.example.auth_service.dto.UpdateUserDTO;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.DepartmentRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.util.RoleUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;
    
    private User applyUpdates(User user, UpdateUserDTO updates) {
        updates.getName().ifPresent(user::setName);
        updates.getEmail().ifPresent(user::setEmail);
        updates.getPosition().ifPresent(user::setPosition);
        updates.getAddress().ifPresent(user::setAddress);
        updates.getPhone().ifPresent(user::setPhone);
        return user;
    }

    // Update SuperAdmin (only by SuperAdmin)
    @PutMapping("/superadmin/{userId}")
    public ResponseEntity<User> updateSuperAdmin(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO userUpdateDTO,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only SUPERADMIN can update other SUPERADMINs");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify the target user is a SUPERADMIN
        if (!user.getRole().getName().equals(Role.RoleType.SUPERADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This endpoint is only for SUPERADMIN updates");
        }

        return ResponseEntity.ok(userRepository.save(applyUpdates(user, userUpdateDTO)));
    }

    // Update Admin (only by SuperAdmin)
    @PutMapping("/admin/{userId}")
    public ResponseEntity<User> updateAdmin(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO userUpdateDTO,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only SUPERADMIN can update ADMINs");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify the target user is an ADMIN
        if (!user.getRole().getName().equals(Role.RoleType.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This endpoint is only for ADMIN updates");
        }

        return ResponseEntity.ok(userRepository.save(applyUpdates(user, userUpdateDTO)));
    }

    // Update User (by SuperAdmin or Admin)
    @PutMapping("/user/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserDTO userUpdateDTO,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser) && !RoleUtils.isAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only SUPERADMIN or ADMIN can update USERs");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Verify the target user is a regular USER
        if (!user.getRole().getName().equals(Role.RoleType.USER)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "This endpoint is only for USER updates");
        }

        return ResponseEntity.ok(userRepository.save(applyUpdates(user, userUpdateDTO)));
    }

    @DeleteMapping("/{userId}")
    @Transactional
    public ResponseEntity<Void> deleteUser(
        @PathVariable Long userId,
        @AuthenticationPrincipal UserDetails currentUser) {
        
        // Get current user's role
        boolean isSuperAdmin = RoleUtils.isSuperAdmin(currentUser);
        boolean isAdmin = RoleUtils.isAdmin(currentUser);
        
        // Get target user
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // Check permissions
        if (targetUser.getRole().getName() == Role.RoleType.SUPERADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete SUPERADMIN");
        }
        
        if (targetUser.getRole().getName() == Role.RoleType.ADMIN && !isSuperAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only SUPERADMIN can delete ADMIN");
        }
        
        if (!isSuperAdmin && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
        
        userRepository.delete(targetUser);
        return ResponseEntity.noContent().build();
    }


}