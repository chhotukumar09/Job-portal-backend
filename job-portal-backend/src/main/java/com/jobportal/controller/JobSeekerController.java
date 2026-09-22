package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobSeekerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seeker")
@RequiredArgsConstructor
@Tag(name = "Job Seeker", description = "Profile, skills, apply, track applications (ROLE: JOB_SEEKER)")
public class JobSeekerController {

    private final JobSeekerService seekerService;
    private final ApplicationService applicationService;

    // ----- Profile -----
    @GetMapping("/profile")
    public ResponseEntity<JobSeekerProfileResponse> getProfile() {
        return ResponseEntity.ok(seekerService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<JobSeekerProfileResponse> updateProfile(@Valid @RequestBody JobSeekerProfileRequest req) {
        return ResponseEntity.ok(seekerService.updateProfile(req));
    }

    // ----- Skills -----
    @PostMapping("/skills")
    public ResponseEntity<JobSeekerProfileResponse> addSkills(@Valid @RequestBody SkillsRequest req) {
        return ResponseEntity.ok(seekerService.addSkills(req.skills()));
    }

    @DeleteMapping("/skills/{skill}")
    public ResponseEntity<JobSeekerProfileResponse> removeSkill(@PathVariable String skill) {
        return ResponseEntity.ok(seekerService.removeSkill(skill));
    }

    // ----- Applications -----
    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApplicationResponse> apply(@PathVariable Long jobId,
                                                     @Valid @RequestBody(required = false) ApplyRequest req) {
        String cover = req == null ? null : req.coverLetter();
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(jobId, cover));
    }

    @GetMapping("/applications")
    public ResponseEntity<PageResponse<ApplicationResponse>> myApplications(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.myApplications(status, page, size));
    }

    @DeleteMapping("/applications/{id}")
    public ResponseEntity<MessageResponse> withdraw(@PathVariable Long id) {
        applicationService.withdraw(id);
        return ResponseEntity.ok(new MessageResponse("Application withdrawn successfully"));
    }
}
