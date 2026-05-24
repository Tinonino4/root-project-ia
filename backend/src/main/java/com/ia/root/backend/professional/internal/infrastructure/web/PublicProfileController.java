package com.ia.root.backend.professional.internal.infrastructure.web;

import com.ia.root.backend.analytics.SkillsData;
import com.ia.root.backend.analytics.SkillsMetricsService;
import com.ia.root.backend.professional.internal.application.ProfessionalService;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.PublicProfileDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/public/profile", version = "1")
@Tag(name = "Public Profile", description = "Consulta pública de perfiles profesionales")
public class PublicProfileController {

    private final ProfessionalService professionalService;
    private final SkillsMetricsService skillsMetricsService;

    public PublicProfileController(ProfessionalService professionalService,
                                   SkillsMetricsService skillsMetricsService) {
        this.professionalService = professionalService;
        this.skillsMetricsService = skillsMetricsService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Obtener perfil público por ID de usuario")
    public ResponseEntity<PublicProfileDTO> getPublicProfile(@PathVariable UUID userId) {
        try {
            UserProfile profile = professionalService.getProfile(userId);
            List<Experience> experiences = professionalService.getExperiences(userId);
            SkillsData skillsData = skillsMetricsService.getSkillsData(userId);

            PublicProfileDTO dto = new PublicProfileDTO(
                profile.getName(),
                profile.getSurname(),
                profile.getJobTitle(),
                profile.getAboutMe(),
                profile.getPhotoUrl(),
                experiences,
                skillsData
            );

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Profile not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}
