package com.ia.root.backend.feedback.internal.infrastructure.web;

import com.ia.root.backend.feedback.internal.application.FeedbackService;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.QuestionnaireViewDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.SubmitQuestionnaireDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/questionnaire", version = "1")
@Tag(name = "Questionnaire", description = "Endpoints públicos para el cuestionario de feedback")
public class QuestionnaireController {

    private final FeedbackService feedbackService;

    public QuestionnaireController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/{urlToken}")
    @Operation(summary = "Obtener cuestionario por token URL (público, sin autenticación)",
        description = "Devuelve las categorías con preguntas y datos de la solicitud. Falla si ya fue completado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cuestionario con categorías y preguntas"),
            @ApiResponse(responseCode = "400", description = "Token no encontrado o cuestionario ya completado", content = @Content)
        })
    public ResponseEntity<QuestionnaireViewDTO> getQuestionnaire(
            @Parameter(description = "Token único de la solicitud de feedback") @PathVariable String urlToken) {
        return ResponseEntity.ok(feedbackService.getQuestionnaire(urlToken));
    }

    @PostMapping("/{urlToken}")
    @Operation(summary = "Enviar respuestas del cuestionario (público, sin autenticación)",
        description = "Registra las respuestas de rating (1-5) por pregunta y las respuestas extra. Recalcula métricas del usuario.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cuestionario enviado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, ya completado, o datos inválidos", content = @Content)
        })
    public ResponseEntity<Map<String, String>> submitQuestionnaire(
            @Parameter(description = "Token único de la solicitud de feedback") @PathVariable String urlToken,
            @RequestBody @Valid SubmitQuestionnaireDTO dto) {
        feedbackService.submitQuestionnaire(urlToken, dto);
        return ResponseEntity.ok(Map.of("message", "Cuestionario enviado exitosamente. ¡Gracias!"));
    }
}
