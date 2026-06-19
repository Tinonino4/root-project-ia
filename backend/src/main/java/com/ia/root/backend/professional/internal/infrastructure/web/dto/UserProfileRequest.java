package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Datos del perfil profesional del usuario")
public record UserProfileRequest(
    @Schema(description = "Nombre", example = "Juan")
    String name,

    @Schema(description = "Apellido", example = "Pérez")
    String surname,

    @Schema(description = "Email de contacto", example = "juan@contacto.com")
    String contactEmail,

    @Schema(description = "Descripción personal")
    String aboutMe,

    @Schema(description = "Ciudad", example = "Madrid")
    String city,

    @Schema(description = "Fecha de nacimiento", example = "1990-05-20")
    LocalDate birthday,

    @Schema(description = "Código postal", example = "28001")
    String zipcode,

    @Schema(description = "Número de teléfono", example = "+34612345678")
    String phoneNumber,

    @Schema(description = "URL de la foto de perfil")
    String photoUrl,

    @Schema(description = "Puesto actual", example = "Senior Developer")
    String jobTitle,

    @Schema(description = "Formación académica", example = "Ingeniería Informática")
    String education,

    @Schema(description = "Nombre de usuario único para la URL amigable", example = "juan-perez")
    String username
) {}
