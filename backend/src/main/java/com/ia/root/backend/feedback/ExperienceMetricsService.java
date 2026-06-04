package com.ia.root.backend.feedback;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ExperienceMetricsService {

    private final JdbcTemplate jdbcTemplate;

    public ExperienceMetricsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Devuelve el total de referencias válidas (completadas y visibles) para el usuario.
     */
    public long getTotalReferencesCount(UUID userId) {
        String sql = "SELECT COUNT(*) FROM cache_requests WHERE user_id = ? AND finished = true AND is_visible = true";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, userId);
        return count != null ? count : 0L;
    }

    /**
     * Calcula y devuelve el desglose de métricas específicas por experiencia profesional.
     */
    public List<ExperienceMetricsDTO> getExperienceMetrics(UUID userId) {
        // Query 1: Experiencias, número de referencias, media de puntuación total y de confianza
        String mainSql = """
            SELECT cr.experience_id, COUNT(DISTINCT cr.id) as ref_count, AVG(fr.rating) as avg_rating, AVG(cr.trust_score) as avg_trust
            FROM cache_requests cr
            LEFT JOIN feedback_responses fr ON fr.cache_request_id = cr.id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY cr.experience_id
        """;

        // Query 2: Desglose de categorías por experiencia
        String categorySql = """
            SELECT cr.experience_id, sc.code, AVG(fr.rating) as avg_rating
            FROM feedback_responses fr
            JOIN skill_questions sq ON sq.id = fr.question_id
            JOIN skill_categories sc ON sc.id = sq.category_id
            JOIN cache_requests cr ON cr.id = fr.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY cr.experience_id, sc.code
        """;

        // Query 3: Distribución de roles por experiencia
        String roleSql = """
            SELECT cr.experience_id, rt.code, COUNT(cr.id) as role_count
            FROM cache_requests cr
            JOIN relationship_types rt ON rt.id = cr.relationship_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY cr.experience_id, rt.code
        """;

        java.util.Map<UUID, java.util.Map<String, Double>> categoriesMap = new java.util.HashMap<>();
        jdbcTemplate.query(categorySql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            String code = rs.getString("code");
            double avgRating = rs.getDouble("avg_rating");
            categoriesMap.computeIfAbsent(expId, k -> new java.util.HashMap<>()).put(code, avgRating);
        }, userId);

        java.util.Map<UUID, java.util.Map<String, Long>> rolesMap = new java.util.HashMap<>();
        jdbcTemplate.query(roleSql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            String code = rs.getString("code");
            long count = rs.getLong("role_count");
            rolesMap.computeIfAbsent(expId, k -> new java.util.HashMap<>()).put(code, count);
        }, userId);

        List<ExperienceMetricsDTO> metrics = new java.util.ArrayList<>();
        jdbcTemplate.query(mainSql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            long refCount = rs.getLong("ref_count");
            double avgRating = rs.getDouble("avg_rating");
            double avgTrust = rs.getDouble("avg_trust");

            java.util.Map<String, Double> catAverages = categoriesMap.getOrDefault(expId, java.util.Map.of());
            java.util.Map<String, Long> relCounts = rolesMap.getOrDefault(expId, java.util.Map.of());

            metrics.add(new ExperienceMetricsDTO(
                expId,
                avgRating,
                refCount,
                catAverages,
                relCounts,
                avgTrust
            ));
        }, userId);

        return metrics;
    }
}
