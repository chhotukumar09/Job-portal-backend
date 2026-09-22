package com.jobportal.controller;

import com.jobportal.dto.JobResponse;
import com.jobportal.dto.PageResponse;
import com.jobportal.enums.JobType;
import com.jobportal.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Public Jobs", description = "Job search & details (login ki zaroorat nahi)")
public class JobController {

    private final JobService jobService;

    /**
     * Example:
     * GET /api/jobs?keyword=java&location=delhi&jobType=FULL_TIME&minSalary=500000&maxExperience=3&skill=spring&page=0&size=10&sortBy=createdAt&direction=desc
     */
    @GetMapping
    public ResponseEntity<PageResponse<JobResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String skill,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(jobService.search(keyword, location, jobType, minSalary,
                maxExperience, skill, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getById(id));
    }
}
