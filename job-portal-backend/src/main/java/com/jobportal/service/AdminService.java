package com.jobportal.service;

import com.jobportal.dto.*;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.enums.Role;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.mapper.EntityMapper;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    // ---------- Users / Recruiters ----------
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> users(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        var result = role == null ? userRepository.findAll(pageable) : userRepository.findByRole(role, pageable);
        return PageResponse.from(result.map(EntityMapper::toUserResponse));
    }

    public UserResponse setUserEnabled(Long id, boolean enabled) {
        User user = findUser(id);
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin account cannot be disabled");
        }
        user.setEnabled(enabled);
        return EntityMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = findUser(id);
        if (user.getRole() == Role.ADMIN) {
            throw new BadRequestException("Admin account cannot be deleted");
        }
        userRepository.delete(user); // profile, jobs, applications cascade se delete
    }

    // ---------- Jobs ----------
    @Transactional(readOnly = true)
    public PageResponse<JobResponse> jobs(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResponse.from(jobRepository.findAll(pageable).map(EntityMapper::toJobResponse));
    }

    public JobResponse setJobActive(Long id, boolean active) {
        Job job = findJob(id);
        job.setActive(active);
        return EntityMapper.toJobResponse(jobRepository.save(job));
    }

    public void deleteJob(Long id) {
        jobRepository.delete(findJob(id));
    }

    // ---------- Reports ----------
    @Transactional(readOnly = true)
    public StatsResponse stats() {
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (ApplicationStatus s : ApplicationStatus.values()) {
            byStatus.put(s.name(), applicationRepository.countByStatus(s));
        }
        return new StatsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.JOB_SEEKER),
                userRepository.countByRole(Role.RECRUITER),
                userRepository.countByRole(Role.ADMIN),
                jobRepository.count(),
                jobRepository.countByActiveTrue(),
                applicationRepository.count(),
                byStatus);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    private Job findJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id " + id));
    }
}
