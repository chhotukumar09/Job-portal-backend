package com.jobportal.dto;

import com.jobportal.enums.JobType;

import java.time.LocalDateTime;
import java.util.Set;

public record JobResponse(
        Long id,
        String title,
        String description,
        String location,
        JobType jobType,
        Double salaryMin,
        Double salaryMax,
        Integer experienceRequired,
        Set<String> skillsRequired,
        boolean active,
        LocalDateTime createdAt,
        Long recruiterId,
        String companyName
) {}
