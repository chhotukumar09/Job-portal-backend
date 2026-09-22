package com.jobportal.dto;

import com.jobportal.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required (SHORTLISTED, ACCEPTED, REJECTED)") ApplicationStatus status
) {}
