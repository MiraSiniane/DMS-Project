package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleType name;

    public enum RoleType {
        SUPERADMIN,
        ADMIN,
        USER
    }
    public RoleType getName() {
        return name;
    }
}