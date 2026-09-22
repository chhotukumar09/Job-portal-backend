package com.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record SkillsRequest(
        @NotEmpty(message = "At least one skill is required")
        Set<@NotBlank(message = "Skill cannot be blank") String> skills
) {}
