package com.ia.root.backend.feedback.internal.infrastructure.web;

import com.ia.root.backend.feedback.internal.application.FeedbackService;
import com.ia.root.backend.feedback.internal.domain.model.CacheRequest;
import com.ia.root.backend.feedback.internal.domain.model.RelationshipType;
import com.ia.root.backend.feedback.internal.domain.model.SkillCategory;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CacheRequestViewDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CreateCacheRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/feedback", version = "1")
@Tag(name = "Feedback", description = "Gestión de solicitudes de feedback (cache requests)")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Obtener catálogo de categorías de skills con sus preguntas",
        responses = @ApiResponse(responseCode = "200", description = "Lista de 5 categorías con 5 preguntas cada una"))
    public ResponseEntity<List<SkillCategory>> getCategories() {
        return ResponseEntity.ok(feedbackService.getCategories());
    }

    @GetMapping("/relationships")
    @Operation(summary = "Obtener catálogo de tipos de relación profesional",
        responses = @ApiResponse(responseCode = "200", description = "Lista de tipos de relación (jefe, compañero, subordinado, cliente, otro)"))
    public ResponseEntity<List<RelationshipType>> getRelationshipTypes() {
        return ResponseEntity.ok(feedbackService.getRelationshipTypes());
    }

    @PostMapping("/requests")
    @Operation(summary = "Crear una solicitud de feedback para una experiencia",
        responses = {
            @ApiResponse(responseCode = "201", description = "Solicitud creada. Se envía email al referente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<CacheRequestViewDTO> createRequest(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid CreateCacheRequestDTO dto) {
        CacheRequest cr = feedbackService.createCacheRequest(userId, dto);
        CacheRequestViewDTO view = new CacheRequestViewDTO(
            cr.getId(), cr.getExperienceId(), cr.getRelationshipId(),
            cr.isStillWorksThere(), cr.getTargetName(), cr.getTargetSurname(),
            cr.getTargetEmail(), cr.getTargetPhone(), cr.isFinished(), cr.isVisible(),
            cr.getTrustScore(), cr.getTrustLevel(), cr.getCreatedAt(), cr.getExtraAnswers()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @GetMapping("/requests")
    @Operation(summary = "Listar todas las solicitudes de feedback del usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<List<CacheRequestViewDTO>> getRequests(
            @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(feedbackService.getCacheRequests(userId));
    }

    @GetMapping("/requests/experience/{experienceId}")
    @Operation(summary = "Listar solicitudes de feedback de una experiencia específica",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de solicitudes de la experiencia"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<List<CacheRequestViewDTO>> getRequestsByExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Parameter(description = "ID de la experiencia laboral") @PathVariable UUID experienceId) {
        return ResponseEntity.ok(feedbackService.getCacheRequestsByExperience(userId, experienceId));
    }

    @GetMapping("/requests/experience/{experienceId}/count")
    @Operation(summary = "Obtener número de feedbacks completados para una experiencia",
        responses = {
            @ApiResponse(responseCode = "200", description = "Número de feedbacks completados"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Long> getCompletedCount(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @Parameter(description = "ID de la experiencia laboral") @PathVariable UUID experienceId) {
        return ResponseEntity.ok(feedbackService.getCompletedCount(userId, experienceId));
    }

    @PatchMapping("/requests/{requestId}/visibility")
    @Operation(summary = "Cambiar la visibilidad pública de una referencia",
        responses = {
            @ApiResponse(responseCode = "200", description = "Visibilidad cambiada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud no encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<CacheRequestViewDTO> toggleVisibility(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID requestId,
            @RequestBody @Valid VisibilityToggleRequest request) {
        CacheRequestViewDTO updated = feedbackService.toggleVisibility(userId, requestId, request.visible());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/requests/{requestId}")
    @Operation(summary = "Eliminar / cancelar una solicitud de feedback",
        responses = {
            @ApiResponse(responseCode = "204", description = "Solicitud eliminada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Void> deleteRequest(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID requestId) {
        feedbackService.deleteCacheRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/requests/{requestId}/remind")
    @Operation(summary = "Reenviar recordatorio de una solicitud de feedback por email",
        responses = {
            @ApiResponse(responseCode = "200", description = "Recordatorio enviado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Map<String, String>> remindRequest(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID requestId) {
        feedbackService.remindCacheRequest(userId, requestId);
        return ResponseEntity.ok(Map.of("message", "Recordatorio enviado correctamente"));
    }

    public record VisibilityToggleRequest(boolean visible) {}
}
