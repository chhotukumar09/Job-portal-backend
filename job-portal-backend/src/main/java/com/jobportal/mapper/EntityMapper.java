package com.jobportal.mapper;

import com.jobportal.dto.*;
import com.jobportal.entity.*;

import java.util.HashSet;

/** Entity -> DTO conversion. Service layer ke @Transactional ke andar call karo (lazy fields ki wajah se). */
public final class EntityMapper {

    private EntityMapper() {}

    public static UserResponse toUserResponse(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getRole(),
                u.isEnabled(), u.getCreatedAt());
    }

    public static JobSeekerProfileResponse toSeekerResponse(JobSeekerProfile p) {
        return new JobSeekerProfileResponse(p.getId(), p.getUser().getId(), p.getUser().getFullName(),
                p.getUser().getEmail(), p.getPhone(), p.getLocation(), p.getHeadline(),
                p.getExperienceYears(), p.getResumeUrl(), new HashSet<>(p.getSkills()));
    }

    public static RecruiterProfileResponse toRecruiterResponse(RecruiterProfile r) {
        return new RecruiterProfileResponse(r.getId(), r.getUser().getId(), r.getUser().getFullName(),
                r.getUser().getEmail(), r.getCompanyName(), r.getCompanyDescription(),
                r.getWebsite(), r.getLocation(), r.getPhone());
    }

    public static JobResponse toJobResponse(Job j) {
        return new JobResponse(j.getId(), j.getTitle(), j.getDescription(), j.getLocation(),
                j.getJobType(), j.getSalaryMin(), j.getSalaryMax(), j.getExperienceRequired(),
                new HashSet<>(j.getSkillsRequired()), j.isActive(), j.getCreatedAt(),
                j.getRecruiter().getId(), j.getRecruiter().getCompanyName());
    }

    public static ApplicationResponse toApplicationResponse(Application a) {
        JobSeekerProfile s = a.getJobSeeker();
        Job j = a.getJob();
        return new ApplicationResponse(a.getId(), j.getId(), j.getTitle(),
                j.getRecruiter().getCompanyName(), s.getId(), s.getUser().getFullName(),
                s.getUser().getEmail(), new HashSet<>(s.getSkills()), s.getResumeUrl(),
                a.getCoverLetter(), a.getStatus(), a.getAppliedAt(), a.getUpdatedAt());
    }
}
