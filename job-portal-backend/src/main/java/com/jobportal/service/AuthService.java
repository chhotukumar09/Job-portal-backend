package com.jobportal.service;

import com.jobportal.dto.AuthResponse;
import com.jobportal.dto.LoginRequest;
import com.jobportal.dto.RegisterRequest;
import com.jobportal.entity.JobSeekerProfile;
import com.jobportal.entity.RecruiterProfile;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobSeekerProfileRepository;
import com.jobportal.repository.RecruiterProfileRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JobSeekerProfileRepository seekerRepository;
    private final RecruiterProfileRepository recruiterRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();

        if (req.role() == Role.ADMIN) {
            throw new BadRequestException("Admin account cannot be registered publicly");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        User user = userRepository.save(User.builder()
                .fullName(req.fullName().trim())
                .email(email)
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .enabled(true)
                .build());

        // role ke hisaab se empty profile auto-create
        if (req.role() == Role.JOB_SEEKER) {
            JobSeekerProfile profile = new JobSeekerProfile();
            profile.setUser(user);
            seekerRepository.save(profile);
        } else {
            RecruiterProfile profile = new RecruiterProfile();
            profile.setUser(user);
            profile.setCompanyName(req.companyName() != null && !req.companyName().isBlank()
                    ? req.companyName().trim() : user.getFullName() + "'s Company");
            recruiterRepository.save(profile);
        }

        return toAuthResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(),
                user.getEmail(), user.getRole());
    }
}
