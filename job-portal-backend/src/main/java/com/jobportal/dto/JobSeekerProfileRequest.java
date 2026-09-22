package com.jobportal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record JobSeekerProfileRequest(
        @Size(max = 20, message = "Phone max 20 characters") String phone,
        @Size(max = 100) String location,
        @Size(max = 150) String headline,
        @PositiveOrZero(message = "Experience cannot be negative") @Max(60) Integer experienceYears,
        @Size(max = 300) String resumeUrl,
        Set<String> skills
) {}
