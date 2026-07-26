package com.ia.root.backend.professional.internal.infrastructure.web;

import com.ia.root.backend.analytics.SkillsData;
import com.ia.root.backend.analytics.SkillsMetricsService;
import com.ia.root.backend.professional.internal.application.ProfessionalService;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.PublicProfileDTO;
import com.ia.root.backend.feedback.ExperienceMetricsDTO;
import com.ia.root.backend.feedback.ExperienceMetricsService;
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
    private final ExperienceMetricsService experienceMetricsService;

    public PublicProfileController(ProfessionalService professionalService,
                                   SkillsMetricsService skillsMetricsService,
                                   ExperienceMetricsService experienceMetricsService) {
        this.professionalService = professionalService;
        this.skillsMetricsService = skillsMetricsService;
        this.experienceMetricsService = experienceMetricsService;
    }

    @GetMapping("/{userIdOrUsername}")
    @Operation(summary = "Obtener perfil público por ID de usuario, ID de perfil o nombre de usuario amigable")
    public ResponseEntity<PublicProfileDTO> getPublicProfile(@PathVariable String userIdOrUsername) {
        try {
            UserProfile profile = professionalService.findProfileByIdOrUserIdOrUsername(userIdOrUsername);
            UUID userId = profile.getUserId();

            List<Experience> experiences = professionalService.getExperiences(userId);
            SkillsData skillsData = skillsMetricsService.getSkillsData(userId);
            long totalRefs = experienceMetricsService.getTotalReferencesCount(userId);
            List<ExperienceMetricsDTO> experienceMetrics = experienceMetricsService.getExperienceMetrics(userId);

            PublicProfileDTO dto = new PublicProfileDTO(
                userId,
                profile.getName(),
                profile.getSurname(),
                profile.getJobTitle(),
                profile.getAboutMe(),
                profile.getPhotoUrl(),
                profile.getUsername(),
                experiences,
                skillsData,
                totalRefs,
                experienceMetrics
            );

            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            if ("Profile not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}
