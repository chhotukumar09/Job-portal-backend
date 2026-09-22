package com.jobportal.service;

import com.jobportal.dto.ApplicationResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ForbiddenException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.mapper.EntityMapper;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerService jobSeekerService;
    private final RecruiterService recruiterService;
    private final JobService jobService;

    // ---------- Job Seeker ----------
    public ApplicationResponse apply(Long jobId, String coverLetter) {
        JobSeekerProfile seeker = jobSeekerService.currentProfile();
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + jobId));

        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer accepting applications");
        }
        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, seeker.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        Application app = new Application();
        app.setJob(job);
        app.setJobSeeker(seeker);
        app.setCoverLetter(coverLetter);
        app.setStatus(ApplicationStatus.PENDING);
        return EntityMapper.toApplicationResponse(applicationRepository.save(app));
    }

    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> myApplications(ApplicationStatus status, int page, int size) {
        JobSeekerProfile seeker = jobSeekerService.currentProfile();
        Pageable pageable = pageable(page, size);
        Page<Application> result = status == null
                ? applicationRepository.findByJobSeekerId(seeker.getId(), pageable)
                : applicationRepository.findByJobSeekerIdAndStatus(seeker.getId(), status, pageable);
        return PageResponse.from(result.map(EntityMapper::toApplicationResponse));
    }

    /** Sirf PENDING application withdraw ho sakti hai. */
    public void withdraw(Long applicationId) {
        JobSeekerProfile seeker = jobSeekerService.currentProfile();
        Application app = findById(applicationId);
        if (!app.getJobSeeker().getId().equals(seeker.getId())) {
            throw new ForbiddenException("You can only withdraw your own applications");
        }
        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("Only PENDING applications can be withdrawn");
        }
        applicationRepository.delete(app);
    }

    // ---------- Recruiter ----------
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> applicantsForJob(Long jobId, ApplicationStatus status, int page, int size) {
        Job job = jobService.getOwnedJob(jobId);
        Pageable pageable = pageable(page, size);
        Page<Application> result = status == null
                ? applicationRepository.findByJobId(job.getId(), pageable)
                : applicationRepository.findByJobIdAndStatus(job.getId(), status, pageable);
        return PageResponse.from(result.map(EntityMapper::toApplicationResponse));
    }

    public ApplicationResponse updateStatus(Long applicationId, ApplicationStatus newStatus) {
        if (newStatus == ApplicationStatus.PENDING) {
            throw new BadRequestException("Cannot set status back to PENDING");
        }
        RecruiterProfile me = recruiterService.currentProfile();
        Application app = findById(applicationId);
        if (!app.getJob().getRecruiter().getId().equals(me.getId())) {
            throw new ForbiddenException("This application does not belong to your job");
        }
        app.setStatus(newStatus);
        return EntityMapper.toApplicationResponse(applicationRepository.save(app));
    }

    // ---------- helpers ----------
    private Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id " + id));
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "appliedAt"));
    }
}
