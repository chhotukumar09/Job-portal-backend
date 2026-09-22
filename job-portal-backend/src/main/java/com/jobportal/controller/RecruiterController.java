package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.RecruiterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
@Tag(name = "Recruiter", description = "Company, jobs, applicants (ROLE: RECRUITER)")
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    // ----- Company profile -----
    @GetMapping("/company")
    public ResponseEntity<RecruiterProfileResponse> getCompany() {
        return ResponseEntity.ok(recruiterService.getCompany());
    }

    @PutMapping("/company")
    public ResponseEntity<RecruiterProfileResponse> updateCompany(@Valid @RequestBody RecruiterProfileRequest req) {
        return ResponseEntity.ok(recruiterService.updateCompany(req));
    }

    // ----- Jobs -----
    @PostMapping("/jobs")
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody JobRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.create(req));
    }

    @GetMapping("/jobs")
    public ResponseEntity<PageResponse<JobResponse>> myJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(jobService.myJobs(page, size));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequest req) {
        return ResponseEntity.ok(jobService.update(id, req));
    }

    /** Job ko band/open karna (soft close). */
    @PatchMapping("/jobs/{id}/active")
    public ResponseEntity<JobResponse> setActive(@PathVariable Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(jobService.setActive(id, value));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<MessageResponse> deleteJob(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Job deleted successfully"));
    }

    // ----- Applicants -----
    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<PageResponse<ApplicationResponse>> applicants(
            @PathVariable Long jobId,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.applicantsForJob(jobId, status, page, size));
    }

    /** Accept / Reject / Shortlist */
    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(applicationService.updateStatus(id, req.status()));
    }
}
