package com.ia.root.backend.analytics;

import com.ia.root.backend.analytics.internal.domain.model.UserSkillsMetrics;
import com.ia.root.backend.analytics.internal.domain.repository.UserSkillsMetricsRepository;
import com.ia.root.backend.feedback.FeedbackCompletedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Map<String, Float> averages = calculateAveragesByCategory(userId);

        UserSkillsMetrics metrics = metricsRepository.findByUserId(userId)
            .orElseGet(() -> UserSkillsMetrics.createForUser(userId));

        metrics.recalculate(
            averages.getOrDefault("TEAMWORK", 0f),
            averages.getOrDefault("SELF_CONFIDENCE", 0f),
            averages.getOrDefault("PROACTIVITY", 0f),
            averages.getOrDefault("INTEGRITY", 0f),
            averages.getOrDefault("FLEXIBILITY", 0f)
        );

        metricsRepository.save(metrics);
    }

    private Map<String, Float> calculateAveragesByCategory(UUID userId) {
        String sql = """
            SELECT sc.code, AVG(fr.rating) as avg_rating
            FROM feedback_responses fr
            JOIN skill_questions sq ON sq.id = fr.question_id
            JOIN skill_categories sc ON sc.id = sq.category_id
            JOIN cache_requests cr ON cr.id = fr.cache_request_id
            WHERE cr.user_id = ? AND cr.finished = true AND cr.is_visible = true
            GROUP BY sc.code
        """;

        java.util.HashMap<String, Float> result = new java.util.HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getString("code"), rs.getFloat("avg_rating"));
        }, userId);

        return result;
    }
}
