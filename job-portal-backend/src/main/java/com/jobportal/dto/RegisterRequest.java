package com.jobportal.dto;

import com.jobportal.enums.Role;
import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be 2-100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 50, message = "Password must be 6-50 characters")
        String password,

        @NotNull(message = "Role is required (JOB_SEEKER or RECRUITER)")
        Role role,

        // sirf recruiter ke liye (optional)
        String companyName
) {}
