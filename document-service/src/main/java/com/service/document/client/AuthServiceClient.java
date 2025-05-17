package com.service.document.client;

import com.service.document.dto.UserInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Collection;

@FeignClient(name = "auth-service", url = "${app.services.auth.url}")
public interface AuthServiceClient {
    
    @GetMapping("/api/auth/me")
    UserInfoDTO getCurrentUser(@RequestHeader("Authorization") String authHeader);
    
    @GetMapping("/api/auth/users/{userId}")
    UserInfoDTO getUserById(@RequestHeader("Authorization") String authHeader, 
                          @PathVariable("userId") Long userId);
    
    @GetMapping("/api/auth/me/departments")
    Collection<Long> getUserDepartments(@RequestHeader("Authorization") String authHeader);
    
    @GetMapping("/api/auth/departments/{departmentId}/access")
    boolean hasDepartmentAccess(@RequestHeader("Authorization") String authHeader, 
                              @PathVariable("departmentId") Long departmentId);
}