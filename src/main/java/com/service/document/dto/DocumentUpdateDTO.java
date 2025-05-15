package com.service.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DocumentUpdateDTO {
    @NotBlank
    private String titleEn;
    
    private String titleEs;
    
    private String description;
    
    @NotNull
    private Long categoryId;
    
    private Long departmentId;
    
    // Additional fields needed for the update method
    private Long ownerId;
    
    private List<Long> allowedDepartments;

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}