package com.example.auth_service.dto;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String oldPassword;  // Not required for SuperAdmin
    private String newPassword;
    private Long userId;         // Only used by SuperAdmin
}
