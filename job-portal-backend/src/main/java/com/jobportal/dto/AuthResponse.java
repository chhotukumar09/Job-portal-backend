package com.jobportal.dto;

import com.jobportal.enums.Role;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String fullName,
        String email,
        Role role
) {}
