package com.jobportal.service;

import com.jobportal.dto.JobRequest;
import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.enums.JobType;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ForbiddenException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.mapper.EntityMapper;
import com.jobportal.repository.JobRepository;
import com.jobportal.specification.JobSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobService {

    private static final List<String> SORTABLE = List.of("createdAt", "salaryMax", "salaryMin", "title", "experienceRequired");

    private final JobRepository jobRepository;
    private final RecruiterService recruiterService;

    // ---------- Recruiter ----------
    public JobResponse create(JobRequest req) {
        validateSalary(req);
        RecruiterProfile recruiter = recruiterService.currentProfile();
        Job job = new Job();
        job.setRecruiter(recruiter);
        apply(job, req);
        return EntityMapper.toJobResponse(jobRepository.save(job));
    }

    public JobResponse update(Long id, JobRequest req) {
        validateSalary(req);
        Job job = getOwnedJob(id);
        apply(job, req);
        return EntityMapper.toJobResponse(jobRepository.save(job));
    }

    public JobResponse setActive(Long id, boolean active) {
        Job job = getOwnedJob(id);
        job.setActive(active);
        return EntityMapper.toJobResponse(jobRepository.save(job));
    }

    public void delete(Long id) {
        jobRepository.delete(getOwnedJob(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> myJobs(int page, int size) {
        RecruiterProfile recruiter = recruiterService.currentProfile();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResponse.from(jobRepository.findByRecruiterId(recruiter.getId(), pageable)
                .map(EntityMapper::toJobResponse));
    }

    /** Recruiter ki apni job (ownership check) - dusri services bhi use karti hain. */
    public Job getOwnedJob(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
        RecruiterProfile me = recruiterService.currentProfile();
        if (!job.getRecruiter().getId().equals(me.getId())) {
            throw new ForbiddenException("You can only manage your own jobs");
        }
        return job;
    }

    // ---------- Public ----------
    @Transactional(readOnly = true)
    public JobResponse getById(Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
        return EntityMapper.toJobResponse(job);
    }

    @Transactional(readOnly = true)
    public PageResponse<JobResponse> search(String keyword, String location, JobType jobType,
                                            Double minSalary, Integer maxExperience, String skill,
                                            int page, int size, String sortBy, String direction) {
        String sortField = SORTABLE.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50), Sort.by(dir, sortField));

        return PageResponse.from(jobRepository
                .findAll(JobSpecification.filter(keyword, location, jobType, minSalary, maxExperience, skill), pageable)
                .map(EntityMapper::toJobResponse));
    }

    // ---------- helpers ----------
    private void apply(Job job, JobRequest req) {
        job.setTitle(req.title().trim());
        job.setDescription(req.description().trim());
        job.setLocation(req.location().trim());
        job.setJobType(req.jobType());
        job.setSalaryMin(req.salaryMin());
        job.setSalaryMax(req.salaryMax());
        job.setExperienceRequired(req.experienceRequired());
        Set<String> skills = req.skillsRequired() == null ? new HashSet<>() :
                req.skillsRequired().stream()
                        .filter(s -> s != null && !s.isBlank())
                        .map(s -> s.trim().toLowerCase())
                        .collect(Collectors.toSet());
        job.getSkillsRequired().clear();
        job.getSkillsRequired().addAll(skills);
    }

    private void validateSalary(JobRequest req) {
        if (req.salaryMin() != null && req.salaryMax() != null && req.salaryMin() > req.salaryMax()) {
            throw new BadRequestException("salaryMin cannot be greater than salaryMax");
        }
    }
}
