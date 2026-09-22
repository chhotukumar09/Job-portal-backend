package com.jobportal.dto;

import com.jobportal.enums.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean enabled,
        LocalDateTime createdAt
) {}
