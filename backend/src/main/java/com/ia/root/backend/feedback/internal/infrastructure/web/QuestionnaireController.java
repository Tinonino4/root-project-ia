package com.ia.root.backend.feedback.internal.infrastructure.web;

import com.ia.root.backend.feedback.internal.application.FeedbackService;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.QuestionnaireViewDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.SubmitQuestionnaireDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Obtener cuestionario por token URL (público, sin autenticación)")
    public ResponseEntity<QuestionnaireViewDTO> getQuestionnaire(@PathVariable String urlToken) {
        return ResponseEntity.ok(feedbackService.getQuestionnaire(urlToken));
    }

    @PostMapping("/{urlToken}")
    @Operation(summary = "Enviar respuestas del cuestionario (público, sin autenticación)")
    public ResponseEntity<Map<String, String>> submitQuestionnaire(
            @PathVariable String urlToken,
            @RequestBody @Valid SubmitQuestionnaireDTO dto) {
        feedbackService.submitQuestionnaire(urlToken, dto);
        return ResponseEntity.ok(Map.of("message", "Cuestionario enviado exitosamente. ¡Gracias!"));
    }
}
