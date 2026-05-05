package com.ia.root.backend.professional.internal.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ExperienceRequest(
    @NotBlank(message = "Company name is required")
    String companyName,
    
    String department,
    
    @NotBlank(message = "Position is required")
    String position,
    
    @NotNull(message = "Start date is required")
    LocalDate startDate,
    
    LocalDate finishDate,
    
    String functions
) {}
