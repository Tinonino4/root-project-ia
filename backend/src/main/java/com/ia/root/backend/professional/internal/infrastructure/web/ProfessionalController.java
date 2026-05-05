package com.ia.root.backend.professional.internal.infrastructure.web;

import com.ia.root.backend.professional.internal.application.ProfessionalService;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.ExperienceRequest;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.UserProfileRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/professional")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfile> getProfile(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(professionalService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfile> updateProfile(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid UserProfileRequest request) {
        return ResponseEntity.ok(professionalService.updateProfile(userId, request));
    }

    @GetMapping("/experiences")
    public ResponseEntity<List<Experience>> getExperiences(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(professionalService.getExperiences(userId));
    }

    @PostMapping("/experiences")
    public ResponseEntity<Experience> addExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid ExperienceRequest request) {
        return ResponseEntity.ok(professionalService.addExperience(userId, request));
    }

    @PutMapping("/experiences/{id}")
    public ResponseEntity<Experience> updateExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id,
            @RequestBody @Valid ExperienceRequest request) {
        return ResponseEntity.ok(professionalService.updateExperience(id, userId, request));
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id) {
        professionalService.deleteExperience(id, userId);
        return ResponseEntity.noContent().build();
    }
}
