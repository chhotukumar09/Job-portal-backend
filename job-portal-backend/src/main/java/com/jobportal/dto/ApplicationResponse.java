package com.jobportal.dto;

import com.jobportal.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.Set;

public record ApplicationResponse(
        Long id,
        Long jobId,
        String jobTitle,
        String companyName,
        Long seekerId,
        String seekerName,
        String seekerEmail,
        Set<String> seekerSkills,
        String resumeUrl,
        String coverLetter,
        ApplicationStatus status,
        LocalDateTime appliedAt,
        LocalDateTime updatedAt
) {}
