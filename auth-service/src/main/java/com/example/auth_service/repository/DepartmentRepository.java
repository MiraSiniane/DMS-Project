package com.example.auth_service.repository;

import com.example.auth_service.model.Department;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id); // New method for update validation
    Department findByName(String name);
    
    @EntityGraph(attributePaths = {"users"}) // Efficiently load users with department

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.users WHERE d.id = :id")
    Optional<Department> findWithUsersById(@Param("id") Long id);
}