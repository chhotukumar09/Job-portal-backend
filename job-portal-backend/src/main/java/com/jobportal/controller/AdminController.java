package com.jobportal.controller;

import com.jobportal.dto.*;
import com.jobportal.enums.Role;
import com.jobportal.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Manage users, recruiters, jobs, reports (ROLE: ADMIN)")
public class AdminController {

    private final AdminService adminService;

    // ----- Users & Recruiters -----
    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> users(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.users(role, page, size));
    }

    @GetMapping("/recruiters")
    public ResponseEntity<PageResponse<UserResponse>> recruiters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.users(Role.RECRUITER, page, size));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserResponse> setUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(adminService.setUserEnabled(id, enabled));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
    }

    // ----- Jobs -----
    @GetMapping("/jobs")
    public ResponseEntity<PageResponse<JobResponse>> jobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.jobs(page, size));
    }

    @PatchMapping("/jobs/{id}/active")
    public ResponseEntity<JobResponse> setJobActive(@PathVariable Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(adminService.setJobActive(id, value));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<MessageResponse> deleteJob(@PathVariable Long id) {
        adminService.deleteJob(id);
        return ResponseEntity.ok(new MessageResponse("Job deleted successfully"));
    }

    // ----- Reports -----
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats() {
        return ResponseEntity.ok(adminService.stats());
    }
}
