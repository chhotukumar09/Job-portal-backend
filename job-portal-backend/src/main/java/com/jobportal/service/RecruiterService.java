package com.jobportal.service;

import com.jobportal.dto.RecruiterProfileRequest;
import com.jobportal.dto.RecruiterProfileResponse;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.mapper.EntityMapper;
import com.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterService {

    private final RecruiterProfileRepository profileRepository;
    private final CurrentUser currentUser;

    public RecruiterProfile currentProfile() {
        Long userId = currentUser.get().getId();
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found"));
    }

    @Transactional(readOnly = true)
    public RecruiterProfileResponse getCompany() {
        return EntityMapper.toRecruiterResponse(currentProfile());
    }

    public RecruiterProfileResponse updateCompany(RecruiterProfileRequest req) {
        RecruiterProfile p = currentProfile();
        p.setCompanyName(req.companyName().trim());
        p.setCompanyDescription(req.companyDescription());
        p.setWebsite(req.website());
        p.setLocation(req.location());
        p.setPhone(req.phone());
        return EntityMapper.toRecruiterResponse(profileRepository.save(p));
    }
}
