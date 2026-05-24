package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Resumen de candidato para el buscador B2B")
public record CandidateSearchDTO(
    @Schema(description = "ID del usuario asociado") UUID userId,
    @Schema(description = "Nombre del candidato") String name,
    @Schema(description = "Apellido del candidato") String surname,
    @Schema(description = "Puesto de trabajo actual") String jobTitle,
    @Schema(description = "URL de la foto de perfil") String photoUrl,
    @Schema(description = "Ciudad de residencia") String city
) {}
