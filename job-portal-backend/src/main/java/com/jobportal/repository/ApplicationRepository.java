package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);

    Page<Application> findByJobSeekerId(Long jobSeekerId, Pageable pageable);
    Page<Application> findByJobSeekerIdAndStatus(Long jobSeekerId, ApplicationStatus status, Pageable pageable);

    Page<Application> findByJobId(Long jobId, Pageable pageable);
    Page<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status, Pageable pageable);

    long countByStatus(ApplicationStatus status);
}
