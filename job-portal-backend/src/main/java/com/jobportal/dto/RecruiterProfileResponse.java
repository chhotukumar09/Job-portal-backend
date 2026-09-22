package com.jobportal.dto;

public record RecruiterProfileResponse(
        Long id,
        Long userId,
        String recruiterName,
        String email,
        String companyName,
        String companyDescription,
        String website,
        String location,
        String phone
) {}
