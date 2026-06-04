package com.ia.root.backend.feedback;

import com.ia.root.backend.feedback.internal.application.ReferenceTrustCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class ReferenceTrustCalculatorTests {

    private JdbcTemplate jdbcTemplate;
    private ReferenceTrustCalculator trustCalculator;

    @BeforeEach
    void setUp() {
        jdbcTemplate = Mockito.mock(JdbcTemplate.class);
        trustCalculator = new ReferenceTrustCalculator(jdbcTemplate);
    }

    @Test
    void testIsCorporateDomain() {
        // Proveedores de correo gratuito (falsos corporativos)
        assertFalse(trustCalculator.isCorporateDomain("user@gmail.com"));
        assertFalse(trustCalculator.isCorporateDomain("test@hotmail.es"));
        assertFalse(trustCalculator.isCorporateDomain("contact@outlook.com"));
        assertFalse(trustCalculator.isCorporateDomain("refer@yahoo.es"));

        // Dominios corporativos reales
        assertTrue(trustCalculator.isCorporateDomain("boss@inditex.com"));
        assertTrue(trustCalculator.isCorporateDomain("manager@google.com"));
        assertTrue(trustCalculator.isCorporateDomain("staff@accenture.es"));
        assertTrue(trustCalculator.isCorporateDomain("hr@spacex.co"));
    }

    @Test
    void testMatchesCompany() {
        // Coincidencias exactas y parciales exitosas
        assertTrue(trustCalculator.matchesCompany("boss@inditex.com", "Inditex S.A."));
        assertTrue(trustCalculator.matchesCompany("developer@google.com", "Google LLC"));
        assertTrue(trustCalculator.matchesCompany("admin@microsoft.es", "Microsoft"));
        assertTrue(trustCalculator.matchesCompany("ceo@spacex.co", "Space Exploration Technologies (SpaceX)"));

        // No coincidencias
        assertFalse(trustCalculator.matchesCompany("boss@inditex.com", "Google"));
        assertFalse(trustCalculator.matchesCompany("friend@gmail.com", "Inditex S.A."));
        assertFalse(trustCalculator.matchesCompany("staff@hotmail.com", "Microsoft"));
        assertFalse(trustCalculator.matchesCompany(null, "Google"));
        assertFalse(trustCalculator.matchesCompany("boss@inditex.com", ""));
    }

    @Test
    void testIsUserRegistered() {
        // Escenario: El usuario está registrado
        Mockito.when(jdbcTemplate.queryForObject(
            eq("SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)"),
            eq(Integer.class),
            eq("registered@micache.com")
        )).thenReturn(1);

        // Escenario: El usuario NO está registrado
        Mockito.when(jdbcTemplate.queryForObject(
            eq("SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)"),
            eq(Integer.class),
            eq("stranger@gmail.com")
        )).thenReturn(0);

        assertTrue(trustCalculator.isUserRegistered("registered@micache.com"));
        assertFalse(trustCalculator.isUserRegistered("stranger@gmail.com"));
        assertFalse(trustCalculator.isUserRegistered(null));
        assertFalse(trustCalculator.isUserRegistered("   "));
    }
}
