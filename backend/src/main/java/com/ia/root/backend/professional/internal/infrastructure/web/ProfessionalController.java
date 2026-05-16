package com.ia.root.backend.professional.internal.infrastructure.web;

import com.ia.root.backend.professional.internal.application.ProfessionalService;
import com.ia.root.backend.professional.internal.domain.model.Experience;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.ExperienceRequest;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.UserProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/professional", version = "1")
@Tag(name = "Professional", description = "Gestión del perfil profesional y experiencias laborales")
public class ProfessionalController {

    private final ProfessionalService professionalService;

    public ProfessionalController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Obtener perfil del usuario autenticado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil del usuario"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<UserProfile> getProfile(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(professionalService.getProfile(userId));
    }

    @PutMapping("/profile")
    @Operation(summary = "Actualizar perfil del usuario autenticado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<UserProfile> updateProfile(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid UserProfileRequest request) {
        return ResponseEntity.ok(professionalService.updateProfile(userId, request));
    }

    @GetMapping("/experiences")
    @Operation(summary = "Listar experiencias laborales del usuario",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de experiencias"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<List<Experience>> getExperiences(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(professionalService.getExperiences(userId));
    }

    @PostMapping("/experiences")
    @Operation(summary = "Añadir experiencia laboral",
        responses = {
            @ApiResponse(responseCode = "200", description = "Experiencia creada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Experience> addExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @RequestBody @Valid ExperienceRequest request) {
        return ResponseEntity.ok(professionalService.addExperience(userId, request));
    }

    @PutMapping("/experiences/{id}")
    @Operation(summary = "Actualizar experiencia laboral",
        responses = {
            @ApiResponse(responseCode = "200", description = "Experiencia actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o experiencia no pertenece al usuario", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Experience> updateExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id,
            @RequestBody @Valid ExperienceRequest request) {
        return ResponseEntity.ok(professionalService.updateExperience(id, userId, request));
    }

    @DeleteMapping("/experiences/{id}")
    @Operation(summary = "Eliminar experiencia laboral",
        responses = {
            @ApiResponse(responseCode = "204", description = "Experiencia eliminada"),
            @ApiResponse(responseCode = "400", description = "Experiencia no pertenece al usuario", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
        })
    public ResponseEntity<Void> deleteExperience(
            @AuthenticationPrincipal(expression = "id") UUID userId,
            @PathVariable UUID id) {
        professionalService.deleteExperience(id, userId);
        return ResponseEntity.noContent().build();
    }
}
