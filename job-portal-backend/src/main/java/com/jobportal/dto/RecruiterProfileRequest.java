package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecruiterProfileRequest(
        @NotBlank(message = "Company name is required") @Size(max = 150) String companyName,
        @Size(max = 2000) String companyDescription,
        @Size(max = 200) String website,
        @Size(max = 100) String location,
        @Size(max = 20) String phone
) {}
