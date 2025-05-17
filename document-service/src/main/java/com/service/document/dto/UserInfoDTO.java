package com.service.document.dto;

import java.util.Set;

public record UserInfoDTO(
    String id,
    String email,
    String firstName,
    String lastName,
    Set<String> roles
) {

    public String getFullName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFullName'");
    }

    public Long getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }}