package com.example.auth_service.service;

import com.example.auth_service.dto.AuthResponse;
import com.example.auth_service.dto.LoginRequest;
import com.example.auth_service.dto.SignupRequest;
import com.example.auth_service.model.Department;
import com.example.auth_service.model.Role;
import com.example.auth_service.model.Role.RoleType;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.DepartmentRepository;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository; // Add this
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
            .token(jwtService.generateToken(userDetails))
            .email(user.getEmail())
            .role(user.getRole().getName().name()) // Changed to getName().name()
            .build();
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
    
        // Get role from database
        Role role = roleRepository.findByName(Role.RoleType.valueOf(request.getRole()))
            .orElseThrow(() -> new RuntimeException("Role not found: " + request.getRole()));
        List<Department> departments = departmentRepository.findAllById(request.getDepartmentIds());

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .position(request.getPosition())
            .role(role) // Use the Role entity
            .departments(departments)
            .address(request.getAddress())
            .phone(request.getPhone())
            .status("inactive")
            .build();

        User savedUser = userRepository.save(user);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());

        return AuthResponse.builder()
            .token(jwtService.generateToken(userDetails))
            .email(savedUser.getEmail())
            .role(savedUser.getRole().getName().name()) // Changed to getName().name()
            .build();
    }

    public AuthResponse signupSuperadmin(SignupRequest request) {
        System.out.println("Checking for existing SuperAdmin...");
        boolean exists = userRepository.existsByRoleName(Role.RoleType.SUPERADMIN);
        System.out.println("SuperAdmin exists: " + exists);
        if (exists) {
            throw new RuntimeException("SuperAdmin already exists");
        }

        Role superadminRole = roleRepository.findByName(Role.RoleType.SUPERADMIN)
            .orElseThrow(() -> new RuntimeException("SUPERADMIN role not found"));

        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .position("System Owner")
            .role(superadminRole)
            .status("active")
            .build();

        User savedUser = userRepository.save(user);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());

        return AuthResponse.builder()
            .token(jwtService.generateToken(userDetails))
            .email(savedUser.getEmail())
            .role(savedUser.getRole().getName().name())
            .build();
    }
}