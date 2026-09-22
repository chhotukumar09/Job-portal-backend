package com.jobportal.dto;

import java.util.Set;

public record JobSeekerProfileResponse(
        Long id,
        Long userId,
        String fullName,
        String email,
        String phone,
        String location,
        String headline,
        Integer experienceYears,
        String resumeUrl,
        Set<String> skills
) {}
