package com.ia.root.backend.analytics.internal.infrastructure.web;

import com.ia.root.backend.analytics.internal.application.SkillsMetricsService;
import com.ia.root.backend.analytics.internal.domain.model.UserSkillsMetrics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/skills", version = "1")
@Tag(name = "Skills Metrics", description = "Métricas agregadas de soft skills del usuario")
public class SkillsController {

    private final SkillsMetricsService metricsService;

    public SkillsController(SkillsMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    @Operation(summary = "Obtener métricas de skills del usuario autenticado")
    public ResponseEntity<UserSkillsMetrics> getMetrics(@AuthenticationPrincipal(expression = "id") UUID userId) {
        UserSkillsMetrics metrics = metricsService.getMetrics(userId);
        if (metrics == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(metrics);
    }
}
