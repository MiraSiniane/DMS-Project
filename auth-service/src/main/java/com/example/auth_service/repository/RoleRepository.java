package com.example.auth_service.repository;

import com.example.auth_service.model.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {
    // For enum-based search
    Optional<Role> findByName(Role.RoleType name);
    
    // For exists check (add this new method)
    boolean existsByName(Role.RoleType name);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.role.name = :roleType")
    boolean existsByRoleName(@Param("roleType") Role.RoleType roleType);
}