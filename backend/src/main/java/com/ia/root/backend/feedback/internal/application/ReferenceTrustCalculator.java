package com.ia.root.backend.feedback.internal.application;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class ReferenceTrustCalculator {

    private final JdbcTemplate jdbcTemplate;

    // Dominios de proveedores de correo gratuito conocidos
    private static final Set<String> FREE_DOMAINS = Set.of(
        "gmail.com", "hotmail.com", "outlook.com", "yahoo.com", "icloud.com",
        "proton.me", "protonmail.com", "aol.com", "live.com", "hotmail.es",
        "yahoo.es", "live.cl", "yandex.com", "mail.com", "zoho.com", "gmx.com", "outlook.es"
    );

    public ReferenceTrustCalculator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Valida si el email utiliza un dominio corporativo/profesional.
     */
    public boolean isCorporateDomain(String email) {
        if (email == null || !email.contains("@")) return false;
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase().trim();
        return !FREE_DOMAINS.contains(domain);
    }

    /**
     * Valida si el dominio del email coincide o se asemeja al nombre de la empresa de la experiencia.
     */
    public boolean matchesCompany(String email, String companyName) {
        if (email == null || !email.contains("@") || companyName == null || companyName.isBlank()) {
            return false;
        }
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase().trim();
        String domainBase = domain.split("\\.")[0].replaceAll("[^a-z0-9]", "");
        String normalizedCompany = companyName.toLowerCase().replaceAll("[^a-z0-9]", "");

        if (domainBase.isEmpty() || normalizedCompany.isEmpty()) return false;

        return normalizedCompany.contains(domainBase) || domainBase.contains(normalizedCompany);
    }

    /**
     * Valida si el email de la referencia pertenece a un usuario ya registrado en MiCaché.
     */
    public boolean isUserRegistered(String email) {
        if (email == null || email.isBlank()) return false;
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)",
            Integer.class,
            email.trim()
        );
        return count != null && count > 0;
    }
}
