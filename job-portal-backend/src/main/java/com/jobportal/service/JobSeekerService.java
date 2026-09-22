package com.jobportal.service;

import com.jobportal.dto.JobSeekerProfileRequest;
import com.jobportal.dto.JobSeekerProfileResponse;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.mapper.EntityMapper;
import com.jobportal.repository.JobSeekerProfileRepository;
import com.jobportal.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobSeekerService {

    private final JobSeekerProfileRepository profileRepository;
    private final CurrentUser currentUser;

    /** Logged-in seeker ka profile entity (dusri services bhi use karti hain). */
    public JobSeekerProfile currentProfile() {
        Long userId = currentUser.get().getId();
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker profile not found"));
    }

    @Transactional(readOnly = true)
    public JobSeekerProfileResponse getProfile() {
        return EntityMapper.toSeekerResponse(currentProfile());
    }

    public JobSeekerProfileResponse updateProfile(JobSeekerProfileRequest req) {
        JobSeekerProfile p = currentProfile();
        p.setPhone(req.phone());
        p.setLocation(req.location());
        p.setHeadline(req.headline());
        p.setExperienceYears(req.experienceYears());
        p.setResumeUrl(req.resumeUrl());
        if (req.skills() != null) {
            p.getSkills().clear();
            p.getSkills().addAll(normalize(req.skills()));
        }
        return EntityMapper.toSeekerResponse(profileRepository.save(p));
    }

    public JobSeekerProfileResponse addSkills(Set<String> skills) {
        JobSeekerProfile p = currentProfile();
        p.getSkills().addAll(normalize(skills));
        return EntityMapper.toSeekerResponse(profileRepository.save(p));
    }

    public JobSeekerProfileResponse removeSkill(String skill) {
        JobSeekerProfile p = currentProfile();
        p.getSkills().remove(skill.trim().toLowerCase());
        return EntityMapper.toSeekerResponse(profileRepository.save(p));
    }

    private Set<String> normalize(Set<String> skills) {
        return skills.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());
    }
}
