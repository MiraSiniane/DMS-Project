// src/main/java/com/example/auth_service/dto/DepartmentListDTO.java
package com.example.auth_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DepartmentListDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private int userCount;
    private int documentCount;
}