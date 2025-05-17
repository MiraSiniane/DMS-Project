package com.example.auth_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String position;
    private String role;
    private List<Long> departmentIds; // For many-to-many assignment
    private String address;
    private String phone;
}