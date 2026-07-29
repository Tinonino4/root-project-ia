package com.ia.root.backend.analytics;

import com.ia.root.backend.analytics.internal.domain.model.UserSkillsMetrics;
import com.ia.root.backend.analytics.internal.domain.repository.UserSkillsMetricsRepository;
import com.ia.root.backend.feedback.FeedbackCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SkillsMetricsService {

    private final UserSkillsMetricsRepository metricsRepository;
    private final JdbcTemplate jdbcTemplate;

    public SkillsMetricsService(UserSkillsMetricsRepository metricsRepository,
                                JdbcTemplate jdbcTemplate) {
        this.metricsRepository = metricsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public UserSkillsMetrics getMetrics(UUID userId) {
        return metricsRepository.findByUserId(userId).orElse(null);
    }

    public SkillsData getSkillsData(UUID userId) {
        return metricsRepository.findByUserId(userId)
            .map(m -> new SkillsData(
                m.getTeamwork(),
                m.getSelfConfidence(),
                m.getProactivity(),
                m.getIntegrity(),
                m.getFlexibility(),
                m.getAverageScore()
            ))
            .orElse(null);
    }

    public MultiLayerSkillsData getMultiLayerSkillsData(UUID userId) {
        SkillsData global = getSkillsData(userId);
        if (global == null) return null;

        SkillsData managers = getSkillsDataByRelationship(userId, 0); // DIRECT_MANAGER
        SkillsData peers = getSkillsDataByRelationship(userId, 1);    // COLLEAGUE
        SkillsData subordinates = getSkillsDataByRelationship(userId, 2); // SUBORDINATE

        return new MultiLayerSkillsData(global, managers, peers, subordinates);
    }

    public ArchetypeDataDTO getArchetypeData(UUID userId) {
        SkillsData global = getSkillsData(userId);
        if (global == null) return null;

        // Fetch top voted virtudes from FORCED_CHOICE questions
        String topVirtuesSql = """
            SELECT bqo.option_text, COUNT(br.id) as votes
            FROM behavioral_responses br
            JOIN behavioral_question_options bqo ON bqo.id = br.selected_option_id
            JOIN behavioral_questions bq ON bq.id = br.question_id
            JOIN cache_requests cr ON cr.id = br.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true AND bq.question_type = 'FORCED_CHOICE'
            GROUP BY bqo.option_text
            ORDER BY votes DESC
            LIMIT 3
        """;

        List<String> topStrengths = new ArrayList<>();
        jdbcTemplate.query(topVirtuesSql, rs -> {
            topStrengths.add(rs.getString("option_text"));
        }, userId);

        if (topStrengths.isEmpty()) {
            topStrengths.add("Calma y resolución bajo picos de alta presión");
            topStrengths.add("Alta autonomía en proyectos desde cero (0 a 1)");
        }

        // Fetch top CULTURAL_FIT selection
        String culturalFitSql = """
            SELECT bqo.option_text, COUNT(br.id) as votes
            FROM behavioral_responses br
            JOIN behavioral_question_options bqo ON bqo.id = br.selected_option_id
            JOIN behavioral_questions bq ON bq.id = br.question_id
            JOIN cache_requests cr ON cr.id = br.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true AND bq.question_type = 'CULTURAL_FIT'
            GROUP BY bqo.option_text
            ORDER BY votes DESC
            LIMIT 1
        """;

        List<String> envOptions = new ArrayList<>();
        jdbcTemplate.query(culturalFitSql, rs -> {
            envOptions.add(rs.getString("option_text"));
        }, userId);

        String envName = envOptions.isEmpty()
            ? "Startup / Scaleup (Ritmo ágil y alta autonomía)"
            : envOptions.get(0);

        ArchetypeDataDTO.IdealEnvironmentDTO env = new ArchetypeDataDTO.IdealEnvironmentDTO(envName, 85);

        List<String> tags = List.of("Respondedor Pragmático", "Comunicador Sintético", "Liderazgo Coach");

        return new ArchetypeDataDTO(tags, topStrengths, env);
    }

    private SkillsData getSkillsDataByRelationship(UUID userId, int relationshipId) {
        String sql = """
            SELECT 
                AVG(bqo.teamwork_pts) as avg_teamwork,
                AVG(bqo.proactivity_pts) as avg_proactivity,
                AVG(bqo.flexibility_pts) as avg_flexibility,
                AVG(bqo.integrity_pts) as avg_integrity,
                AVG(bqo.leadership_pts) as avg_leadership
            FROM behavioral_responses br
            JOIN behavioral_question_options bqo ON bqo.id = br.selected_option_id
            JOIN cache_requests cr ON cr.id = br.cache_request_id
            WHERE cr.user_id = ? AND cr.relationship_id = ? AND cr.finished = true AND cr.is_visible = true
        """;

        List<SkillsData> list = new ArrayList<>();
        jdbcTemplate.query(sql, rs -> {
            float teamwork = rs.getFloat("avg_teamwork");
            float proactivity = rs.getFloat("avg_proactivity");
            float flexibility = rs.getFloat("avg_flexibility");
            float integrity = rs.getFloat("avg_integrity");
            float leadership = rs.getFloat("avg_leadership");

            if (rs.wasNull() || (teamwork == 0 && proactivity == 0 && flexibility == 0)) {
                return;
            }

            float avg = (teamwork + proactivity + flexibility + integrity + leadership) / 5.0f;
            list.add(new SkillsData(teamwork, leadership, proactivity, integrity, flexibility, avg));
        }, userId, relationshipId);

        return list.isEmpty() ? null : list.get(0);
    }

    @EventListener
    @Transactional
    public void onFeedbackCompleted(FeedbackCompletedEvent event) {
        recalculateForUser(event.userId());
    }

    @Transactional
    public void initializeForUser(UUID userId) {
        if (metricsRepository.findByUserId(userId).isEmpty()) {
            metricsRepository.save(UserSkillsMetrics.createForUser(userId));
        }
    }

    private void recalculateForUser(UUID userId) {
        Map<String, Float> averages = calculateGlobalBehavioralAverages(userId);

        UserSkillsMetrics metrics = metricsRepository.findByUserId(userId)
            .orElseGet(() -> UserSkillsMetrics.createForUser(userId));

        metrics.recalculate(
            averages.getOrDefault("TEAMWORK", 0f),
            averages.getOrDefault("LEADERSHIP", 0f),
            averages.getOrDefault("PROACTIVITY", 0f),
            averages.getOrDefault("INTEGRITY", 0f),
            averages.getOrDefault("FLEXIBILITY", 0f)
        );

        metricsRepository.save(metrics);

        // Also persist role specific metrics to user_role_skills_metrics table
        updateRoleMetricsTable(userId);
    }

    private Map<String, Float> calculateGlobalBehavioralAverages(UUID userId) {
        String sql = """
            SELECT 
                AVG(bqo.teamwork_pts) as avg_teamwork,
                AVG(bqo.proactivity_pts) as avg_proactivity,
                AVG(bqo.flexibility_pts) as avg_flexibility,
                AVG(bqo.integrity_pts) as avg_integrity,
                AVG(bqo.leadership_pts) as avg_leadership
            FROM behavioral_responses br
            JOIN behavioral_question_options bqo ON bqo.id = br.selected_option_id
            JOIN cache_requests cr ON cr.id = br.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
        """;

        Map<String, Float> result = new java.util.HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put("TEAMWORK", rs.getFloat("avg_teamwork"));
            result.put("PROACTIVITY", rs.getFloat("avg_proactivity"));
            result.put("FLEXIBILITY", rs.getFloat("avg_flexibility"));
            result.put("INTEGRITY", rs.getFloat("avg_integrity"));
            result.put("LEADERSHIP", rs.getFloat("avg_leadership"));
        }, userId);

        return result;
    }

    private void updateRoleMetricsTable(UUID userId) {
        int[] relationships = {0, 1, 2};
        String[] roleCodes = {"DIRECT_MANAGER", "COLLEAGUE", "SUBORDINATE"};

        for (int i = 0; i < relationships.length; i++) {
            int relId = relationships[i];
            String roleCode = roleCodes[i];
            SkillsData sd = getSkillsDataByRelationship(userId, relId);
            if (sd != null) {
                String upsertSql = """
                    INSERT INTO user_role_skills_metrics (user_id, role_code, teamwork, self_confidence, proactivity, integrity, flexibility, average_score)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (user_id, role_code) DO UPDATE SET
                        teamwork = EXCLUDED.teamwork,
                        self_confidence = EXCLUDED.self_confidence,
                        proactivity = EXCLUDED.proactivity,
                        integrity = EXCLUDED.integrity,
                        flexibility = EXCLUDED.flexibility,
                        average_score = EXCLUDED.average_score,
                        updated_at = CURRENT_TIMESTAMP
                """;
                jdbcTemplate.update(upsertSql, userId, roleCode, sd.teamwork(), sd.selfConfidence(), sd.proactivity(), sd.integrity(), sd.flexibility(), sd.averageScore());
            }
        }
    }
}
