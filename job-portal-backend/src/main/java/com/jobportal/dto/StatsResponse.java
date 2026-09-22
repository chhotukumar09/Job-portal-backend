package com.jobportal.dto;

import java.util.Map;

public record StatsResponse(
        long totalUsers,
        long jobSeekers,
        long recruiters,
        long admins,
        long totalJobs,
        long activeJobs,
        long totalApplications,
        Map<String, Long> applicationsByStatus
) {}
