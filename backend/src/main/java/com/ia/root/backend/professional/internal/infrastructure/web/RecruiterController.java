package com.ia.root.backend.professional.internal.infrastructure.web;

import com.ia.root.backend.professional.internal.application.ProfessionalService;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.CandidateSearchDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/recruiter", version = "1")
@Tag(name = "Recruiter", description = "Endpoints B2B para reclutadores y búsqueda de talento")
public class RecruiterController {

    private final ProfessionalService professionalService;

    public RecruiterController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }

    @GetMapping("/candidates")
    @Operation(summary = "Buscar candidatos por palabra clave (nombre, puesto, ciudad)",
        description = "Devuelve una lista resumida de candidatos que coinciden con la palabra clave.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Búsqueda procesada exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
        })
    public ResponseEntity<List<CandidateSearchDTO>> searchCandidates(
            @Parameter(description = "Texto libre de búsqueda") @RequestParam String query) {
        return ResponseEntity.ok(professionalService.searchCandidates(query));
    }
}
