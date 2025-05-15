package com.example.auth_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class DepartmentAssignmentDTO {
    private Long userId;
    private Long departmentId;
}