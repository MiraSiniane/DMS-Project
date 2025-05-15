package com.example.auth_service.controller;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.PasswordChangeDTO;
import com.example.auth_service.dto.SignupRequest;
import com.example.auth_service.model.Department;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.service.AuthService;
import com.example.auth_service.util.RoleUtils;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository; 
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> signup(
            @RequestBody SignupRequest request,
            @RequestHeader("Authorization") String token
    ) {
        // Only Superadmin can access this endpoint (enforced via SecurityConfig)
        return ResponseEntity.ok(authService.signup(request));
    }
    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(@AuthenticationPrincipal UserDetails user) {
        return Map.of(
            "username", user.getUsername(),
            "authorities", user.getAuthorities()
        );
    }
    @GetMapping("/test-protected")  // Add this endpoint
    public String testProtected() {
        return "This is a protected endpoint";
    }

    @PostMapping("/register-superadmin")
    public ResponseEntity<AuthResponse> registerSuperadmin(
        @RequestBody SignupRequest request
    ) {
        if (userRepository.existsByRoleName(com.example.auth_service.model.Role.RoleType.SUPERADMIN)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "SuperAdmin already exists"
            );
        }
        return ResponseEntity.ok(authService.signupSuperadmin(request));
    }

    @PostMapping("/admin/create-user")
    public ResponseEntity<AuthResponse> createUser(
        @RequestBody SignupRequest request,
        @AuthenticationPrincipal UserDetails adminUser
    ) {
        // Verify admin role (either ADMIN or SUPERADMIN)
        if (!isAdmin(adminUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can create users");
        }
        // Force role=USER for admin-created users
        request.setRole("USER");
        return ResponseEntity.ok(authService.signup(request));
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
            .anyMatch(auth -> 
                auth.getAuthority().equals("ROLE_ADMIN") || 
                auth.getAuthority().equals("ROLE_SUPERADMIN")
            );
    }



    @PostMapping("/change-password")
    public ResponseEntity<String> changeOwnPassword(
            @RequestBody PasswordChangeDTO passwordChange,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        // Verify old password for non-SuperAdmins
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            if (!passwordEncoder.matches(passwordChange.getOldPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect old password");
            }
        }
        
        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/admin/change-password")
    public ResponseEntity<String> changeUserPassword(
            @RequestBody PasswordChangeDTO passwordChange,
            @AuthenticationPrincipal UserDetails currentUser) {
        
        if (!RoleUtils.isSuperAdmin(currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        User user = userRepository.findById(passwordChange.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        
        user.setPassword(passwordEncoder.encode(passwordChange.getNewPassword()));
        userRepository.save(user);
        
        return ResponseEntity.ok("User password changed successfully");
    }
       
}