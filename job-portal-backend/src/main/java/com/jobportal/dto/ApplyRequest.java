package com.jobportal.dto;

import jakarta.validation.constraints.Size;

public record ApplyRequest(
        @Size(max = 2000, message = "Cover letter max 2000 characters") String coverLetter
) {}
