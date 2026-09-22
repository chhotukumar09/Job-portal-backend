package com.jobportal.dto;

import com.jobportal.enums.JobType;
import jakarta.validation.constraints.*;

import java.util.Set;

public record JobRequest(
        @NotBlank(message = "Title is required") @Size(max = 150) String title,
        @NotBlank(message = "Description is required") @Size(max = 5000) String description,
        @NotBlank(message = "Location is required") @Size(max = 100) String location,
        @NotNull(message = "Job type is required") JobType jobType,
        @PositiveOrZero(message = "Salary cannot be negative") Double salaryMin,
        @PositiveOrZero(message = "Salary cannot be negative") Double salaryMax,
        @PositiveOrZero(message = "Experience cannot be negative") Integer experienceRequired,
        Set<String> skillsRequired
) {}
