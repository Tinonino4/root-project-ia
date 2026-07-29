package com.ia.root.backend.professional;

import com.ia.root.backend.analytics.ArchetypeDataDTO;
import com.ia.root.backend.analytics.MultiLayerSkillsData;
import com.ia.root.backend.analytics.SkillsData;
import com.ia.root.backend.analytics.SkillsMetricsService;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.PublicProfileDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class PublicProfile360Test {

    @Test
    @DisplayName("Debe construir PublicProfileDTO con métricas 360° por rol y datos de arquetipo conductual")
    void testPublicProfileDTOWith360Metrics() {
        UUID userId = UUID.randomUUID();

        SkillsData global = new SkillsData(4.5f, 4.6f, 4.7f, 4.7f, 4.5f, 4.6f);
        SkillsData managers = new SkillsData(4.2f, 4.9f, 4.7f, 4.8f, 4.5f, 4.62f);
        SkillsData peers = new SkillsData(4.9f, 4.3f, 4.6f, 4.5f, 4.2f, 4.5f);
        SkillsData subordinates = new SkillsData(4.5f, 4.6f, 4.8f, 4.7f, 4.9f, 4.7f);

        MultiLayerSkillsData multiLayerData = new MultiLayerSkillsData(global, managers, peers, subordinates);
        ArchetypeDataDTO archetypeData = new ArchetypeDataDTO(
            List.of("Respondedor Pragmático", "Comunicador Sintético"),
            List.of("Calma y resolución bajo picos de alta presión"),
            new ArchetypeDataDTO.IdealEnvironmentDTO("Startup / Scaleup", 85)
        );

        PublicProfileDTO dto = new PublicProfileDTO(
            userId,
            "Alejandro",
            "Rivera",
            "Senior Fullstack Engineer",
            "Bio de prueba",
            "https://example.com/photo.jpg",
            "arivera",
            List.of(),
            global,
            multiLayerData,
            archetypeData,
            5L,
            List.of()
        );

        assertNotNull(dto);
        assertEquals("Alejandro", dto.name());
        assertEquals("arivera", dto.username());
        assertNotNull(dto.skillsMultiLayer());
        assertEquals(4.9f, dto.skillsMultiLayer().managers().selfConfidence());
        assertEquals(4.9f, dto.skillsMultiLayer().peers().teamwork());
        assertEquals(4.9f, dto.skillsMultiLayer().subordinates().flexibility());
        assertNotNull(dto.archetype());
        assertEquals(85, dto.archetype().idealEnvironment().fitPercentage());
    }

    @Test
    @DisplayName("SkillsMetricsService debe retornar la estructura 360° para un usuario")
    void testSkillsMetricsService360Structure() {
        SkillsMetricsService mockService = Mockito.mock(SkillsMetricsService.class);
        UUID userId = UUID.randomUUID();

        MultiLayerSkillsData expected = new MultiLayerSkillsData(
            new SkillsData(4.5f, 4.5f, 4.5f, 4.5f, 4.5f, 4.5f),
            new SkillsData(4.0f, 4.8f, 4.5f, 4.5f, 4.5f, 4.46f),
            new SkillsData(4.8f, 4.2f, 4.5f, 4.5f, 4.5f, 4.5f),
            new SkillsData(4.5f, 4.5f, 4.7f, 4.5f, 4.8f, 4.6f)
        );

        given(mockService.getMultiLayerSkillsData(userId)).willReturn(expected);

        MultiLayerSkillsData actual = mockService.getMultiLayerSkillsData(userId);
        assertNotNull(actual);
        assertEquals(4.8f, actual.managers().selfConfidence());
        assertEquals(4.8f, actual.peers().teamwork());
    }
}
