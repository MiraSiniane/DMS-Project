package com.example.auth_service.dto;

import lombok.Data;

@Data
public class UpdateSuperAdminDTO {
    private String name;
    private String email;
    private String position;
    private String address;
    private String phone;
}