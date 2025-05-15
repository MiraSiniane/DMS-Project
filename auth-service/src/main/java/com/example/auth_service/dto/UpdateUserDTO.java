package com.example.auth_service.dto;

import lombok.Data;

import java.util.List;
import java.util.Optional;

@Data
public class UpdateUserDTO {
    private Optional<String> name = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Optional<String> position = Optional.empty();
    private Optional<String> address = Optional.empty();
    private Optional<String> phone = Optional.empty();
}