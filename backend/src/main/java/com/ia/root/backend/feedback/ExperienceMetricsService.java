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
        // Query 1: Experiencias, número de referencias y media de confianza
        String mainSql = """
            SELECT cr.experience_id, COUNT(DISTINCT cr.id) as ref_count, AVG(cr.trust_score) as avg_trust
            FROM cache_requests cr
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY cr.experience_id
        """;

        // Query 2a: Desglose de categorías conductuales 360° por experiencia
        String behavioralCategorySql = """
            SELECT 
                cr.experience_id,
                AVG(bqo.teamwork_pts) as avg_teamwork,
                AVG(bqo.proactivity_pts) as avg_proactivity,
                AVG(bqo.flexibility_pts) as avg_flexibility,
                AVG(bqo.integrity_pts) as avg_integrity,
                AVG(bqo.leadership_pts) as avg_leadership
            FROM behavioral_responses br
            JOIN behavioral_question_options bqo ON bqo.id = br.selected_option_id
            JOIN cache_requests cr ON cr.id = br.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY cr.experience_id
        """;

        // Query 2b: Legacy feedback responses fallback
        String legacyCategorySql = """
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

        // Populate from behavioral responses (scaled to 0-5.0 range by dividing by 20.0)
        jdbcTemplate.query(behavioralCategorySql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            java.util.Map<String, Double> map = new java.util.HashMap<>();
            map.put("TEAMWORK", rs.getDouble("avg_teamwork") / 20.0);
            map.put("PROACTIVITY", rs.getDouble("avg_proactivity") / 20.0);
            map.put("FLEXIBILITY", rs.getDouble("avg_flexibility") / 20.0);
            map.put("INTEGRITY", rs.getDouble("avg_integrity") / 20.0);
            map.put("SELF_CONFIDENCE", rs.getDouble("avg_leadership") / 20.0);
            categoriesMap.put(expId, map);
        }, userId);

        // Fallback for experiences not present in behavioral responses (legacy ratings)
        jdbcTemplate.query(legacyCategorySql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            if (!categoriesMap.containsKey(expId)) {
                String code = rs.getString("code");
                double avgRating = rs.getDouble("avg_rating");
                categoriesMap.computeIfAbsent(expId, k -> new java.util.HashMap<>()).put(code, avgRating);
            }
        }, userId);

        java.util.Map<UUID, java.util.Map<String, Long>> rolesMap = new java.util.HashMap<>();
        jdbcTemplate.query(roleSql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            String code = rs.getString("code");
            long count = rs.getLong("role_count");
            rolesMap.computeIfAbsent(expId, k -> new java.util.HashMap<>()).put(code, count);
        }, userId);

        java.util.Map<UUID, List<TestimonialDTO>> testimonialsMap = new java.util.HashMap<>();
        String testimonialSql = """
            SELECT cr.experience_id, cr.extra_answers->>'comments' as comment, rt.code as relationship_code, 
                   cr.trust_level, cr.trust_score, cr.created_at, cr.target_name, cr.target_surname
            FROM cache_requests cr
            JOIN relationship_types rt ON rt.id = cr.relationship_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true 
              AND cr.extra_answers->>'comments' IS NOT NULL 
              AND cr.extra_answers->>'comments' <> ''
            ORDER BY cr.created_at DESC
        """;
        jdbcTemplate.query(testimonialSql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            String comment = rs.getString("comment");
            String relCode = rs.getString("relationship_code");
            String trustLevel = rs.getString("trust_level");
            int trustScore = rs.getInt("trust_score");
            java.sql.Timestamp ts = rs.getTimestamp("created_at");
            java.time.ZonedDateTime createdAt = ts != null ? java.time.ZonedDateTime.ofInstant(ts.toInstant(), java.time.ZoneId.systemDefault()) : null;
            String name = rs.getString("target_name");
            String surname = rs.getString("target_surname");

            TestimonialDTO t = new TestimonialDTO(expId, comment, relCode, trustLevel, trustScore, name, surname, createdAt);
            testimonialsMap.computeIfAbsent(expId, k -> new java.util.ArrayList<>()).add(t);
        }, userId);

        List<ExperienceMetricsDTO> metrics = new java.util.ArrayList<>();
        jdbcTemplate.query(mainSql, rs -> {
            UUID expId = (UUID) rs.getObject("experience_id");
            long refCount = rs.getLong("ref_count");
            double avgTrust = rs.getDouble("avg_trust");

            java.util.Map<String, Double> catAverages = categoriesMap.getOrDefault(expId, java.util.Map.of());
            double avgRating = catAverages.values().isEmpty() 
                ? 0.0 
                : catAverages.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            java.util.Map<String, Long> relCounts = rolesMap.getOrDefault(expId, java.util.Map.of());
            List<TestimonialDTO> testimonials = testimonialsMap.getOrDefault(expId, java.util.List.of());

            metrics.add(new ExperienceMetricsDTO(
                expId,
                avgRating,
                refCount,
                catAverages,
                relCounts,
                avgTrust,
                testimonials
            ));
        }, userId);

        return metrics;
    }
}
