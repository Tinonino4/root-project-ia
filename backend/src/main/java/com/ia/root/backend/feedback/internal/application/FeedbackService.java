package com.ia.root.backend.feedback.internal.application;

import com.ia.root.backend.feedback.FeedbackCompletedEvent;
import com.ia.root.backend.feedback.FeedbackCompletedNotificationEvent;
import com.ia.root.backend.feedback.FeedbackReminderRequestedEvent;
import com.ia.root.backend.feedback.FeedbackRequestCreatedEvent;
import com.ia.root.backend.feedback.internal.domain.model.CacheRequest;
import com.ia.root.backend.feedback.internal.domain.model.FeedbackResponse;
import com.ia.root.backend.feedback.internal.domain.model.RelationshipType;
import com.ia.root.backend.feedback.internal.domain.model.SkillCategory;
import com.ia.root.backend.feedback.internal.domain.repository.CacheRequestRepository;
import com.ia.root.backend.feedback.internal.domain.repository.FeedbackResponseRepository;
import com.ia.root.backend.feedback.internal.domain.repository.RelationshipTypeRepository;
import com.ia.root.backend.feedback.internal.domain.repository.SkillCategoryRepository;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CacheRequestViewDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CreateCacheRequestDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.QuestionnaireViewDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.SubmitQuestionnaireDTO;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import com.ia.root.backend.feedback.internal.domain.model.BehavioralQuestion;
import com.ia.root.backend.feedback.internal.domain.model.BehavioralResponse;
import com.ia.root.backend.feedback.internal.domain.repository.BehavioralQuestionRepository;
import com.ia.root.backend.feedback.internal.domain.repository.BehavioralResponseRepository;

@Service
@Transactional(readOnly = true)
public class FeedbackService {

    private final CacheRequestRepository cacheRequestRepository;
    private final FeedbackResponseRepository feedbackResponseRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final RelationshipTypeRepository relationshipTypeRepository;
    private final BehavioralQuestionRepository behavioralQuestionRepository;
    private final BehavioralResponseRepository behavioralResponseRepository;
    private final ApplicationEventPublisher events;
    private final JdbcTemplate jdbcTemplate;
    private final ReferenceTrustCalculator trustCalculator;

    public FeedbackService(CacheRequestRepository cacheRequestRepository,
                           FeedbackResponseRepository feedbackResponseRepository,
                           SkillCategoryRepository skillCategoryRepository,
                           RelationshipTypeRepository relationshipTypeRepository,
                           BehavioralQuestionRepository behavioralQuestionRepository,
                           BehavioralResponseRepository behavioralResponseRepository,
                           ApplicationEventPublisher events,
                           JdbcTemplate jdbcTemplate,
                           ReferenceTrustCalculator trustCalculator) {
        this.cacheRequestRepository = cacheRequestRepository;
        this.feedbackResponseRepository = feedbackResponseRepository;
        this.skillCategoryRepository = skillCategoryRepository;
        this.relationshipTypeRepository = relationshipTypeRepository;
        this.behavioralQuestionRepository = behavioralQuestionRepository;
        this.behavioralResponseRepository = behavioralResponseRepository;
        this.events = events;
        this.jdbcTemplate = jdbcTemplate;
        this.trustCalculator = trustCalculator;
    }

    // ── Catálogos ─────────────────────────────────────────────

    public List<SkillCategory> getCategories() {
        return skillCategoryRepository.findAllByOrderByPositionAsc();
    }

    public List<RelationshipType> getRelationshipTypes() {
        return relationshipTypeRepository.findAllByOrderByPositionAsc();
    }

    // ── Crear solicitud de feedback ──────────────────────────

    @Transactional
    public CacheRequest createCacheRequest(UUID userId, CreateCacheRequestDTO dto) {
        String userEmail = lookupUserEmail(userId);
        if (dto.targetEmail() != null && dto.targetEmail().trim().equalsIgnoreCase(userEmail.trim())) {
            throw new IllegalArgumentException("No puedes solicitar feedback a tu propio correo electrónico");
        }

        String urlToken = UUID.randomUUID().toString();

        CacheRequest cr = CacheRequest.create(
            userId, dto.experienceId(), dto.relationshipId(),
            dto.stillWorksThere(), dto.targetName(), dto.targetSurname(),
            dto.targetEmail(), dto.targetPhone(), urlToken
        );

        CacheRequest saved = cacheRequestRepository.save(cr);

        String userName = lookupUserName(userId);
        String companyName = lookupExperienceCompanyName(dto.experienceId());

        events.publishEvent(new FeedbackRequestCreatedEvent(
            saved.getId(), userId, userName,
            dto.targetName(), dto.targetSurname(), dto.targetEmail(),
            companyName, urlToken
        ));

        return saved;
    }

    // ── Eliminar y recordar solicitud de feedback ────────────

    @Transactional
    public void deleteCacheRequest(UUID userId, UUID requestId) {
        CacheRequest cr = cacheRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!cr.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta solicitud");
        }

        cacheRequestRepository.delete(cr);

        // Si la solicitud estaba completada y era visible, recalculamos las métricas
        if (cr.isFinished() && cr.isVisible()) {
            events.publishEvent(new FeedbackCompletedEvent(cr.getId(), cr.getUserId(), cr.getExperienceId()));
        }
    }

    @Transactional
    public void remindCacheRequest(UUID userId, UUID requestId) {
        CacheRequest cr = cacheRequestRepository.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!cr.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para esta solicitud");
        }

        if (cr.isFinished()) {
            throw new IllegalArgumentException("Esta solicitud ya ha sido completada");
        }

        String userName = lookupUserName(userId);
        String companyName = lookupExperienceCompanyName(cr.getExperienceId());

        events.publishEvent(new FeedbackReminderRequestedEvent(
            cr.getId(), userId, userName,
            cr.getTargetName(), cr.getTargetSurname(), cr.getTargetEmail(),
            companyName, cr.getUrlToken()
        ));
    }

    // ── Listar solicitudes del usuario ──────────────────────

    public List<CacheRequestViewDTO> getCacheRequests(UUID userId) {
        return cacheRequestRepository.findByUserId(userId).stream()
            .map(this::toViewDTO)
            .toList();
    }

    public List<CacheRequestViewDTO> getCacheRequestsByExperience(UUID userId, UUID experienceId) {
        return cacheRequestRepository.findByUserIdAndExperienceId(userId, experienceId).stream()
            .map(this::toViewDTO)
            .toList();
    }

    public long getCompletedCount(UUID userId, UUID experienceId) {
        return cacheRequestRepository.countByUserIdAndExperienceIdAndFinishedTrue(userId, experienceId);
    }

    // ── Cuestionario público (sin auth) ─────────────────────

    public QuestionnaireViewDTO getQuestionnaire(String urlToken) {
        CacheRequest cr = cacheRequestRepository.findByUrlToken(urlToken)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (cr.isFinished()) {
            throw new IllegalArgumentException("Este cuestionario ya fue completado");
        }

        String candidateName = lookupUserName(cr.getUserId());
        String companyName = lookupExperienceCompanyName(cr.getExperienceId());
        String relationshipCode = lookupRelationshipCode(cr.getRelationshipId());

        List<BehavioralQuestion> questions = behavioralQuestionRepository
            .findByRelationshipTypeIdOrderByPositionAsc(cr.getRelationshipId());

        List<QuestionnaireViewDTO.BehavioralQuestionDTO> questionDTOs = questions.stream()
            .map(q -> new QuestionnaireViewDTO.BehavioralQuestionDTO(
                q.getId(),
                q.getQuestionCode(),
                q.getQuestionType(),
                q.getQuestionText(),
                q.getPosition(),
                q.getOptions().stream()
                    .map(o -> new QuestionnaireViewDTO.OptionDTO(o.getId(), o.getOptionCode(), o.getOptionText(), o.getPosition()))
                    .toList()
            ))
            .toList();

        return new QuestionnaireViewDTO(
            cr.getId(), cr.getUserId(), candidateName,
            cr.getExperienceId(), companyName,
            cr.getRelationshipId(), relationshipCode,
            questionDTOs
        );
    }

    @Transactional
    public void submitQuestionnaire(String urlToken, SubmitQuestionnaireDTO dto) {
        CacheRequest cr = cacheRequestRepository.findByUrlToken(urlToken)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (cr.isFinished()) {
            throw new IllegalArgumentException("Este cuestionario ya fue completado");
        }

        if (dto.answers() != null) {
            for (SubmitQuestionnaireDTO.BehavioralAnswer ans : dto.answers()) {
                if (ans.selectedOptionIds() != null) {
                    for (UUID optionId : ans.selectedOptionIds()) {
                        behavioralResponseRepository.save(
                            BehavioralResponse.create(cr.getId(), ans.questionId(), optionId)
                        );
                    }
                }
            }
        }
        behavioralResponseRepository.flush();

        // Calcular Trust Score
        int score = 0;
        String email = cr.getTargetEmail();
        String company = lookupExperienceCompanyName(cr.getExperienceId());

        if (trustCalculator.isCorporateDomain(email)) {
            score += 30;
        }
        if (trustCalculator.matchesCompany(email, company)) {
            score += 40;
        }
        if (trustCalculator.isUserRegistered(email)) {
            score += 20;
        }
        if (cr.getTargetPhone() != null && !cr.getTargetPhone().isBlank()) {
            score += 10;
        }

        String level = "BASICO";
        if (score >= 80) {
            level = "EXCELENTE";
        } else if (score >= 50) {
            level = "ALTO";
        } else if (score >= 30) {
            level = "MEDIO";
        }

        cr.setTrustScore(score);
        cr.setTrustLevel(level);

        java.util.Map<String, Object> extra = dto.extraAnswers() != null ? new java.util.HashMap<>(dto.extraAnswers()) : new java.util.HashMap<>();
        if (dto.comments() != null && !dto.comments().isBlank()) {
            extra.put("comments", dto.comments().trim());
        }

        cr.markFinished(extra);
        cacheRequestRepository.saveAndFlush(cr);

        events.publishEvent(new FeedbackCompletedEvent(cr.getId(), cr.getUserId(), cr.getExperienceId()));

        // Publicar evento para notificar al candidato
        String candidateName = lookupUserName(cr.getUserId());
        String candidateEmail = lookupUserEmail(cr.getUserId());
        String refereeName = cr.getTargetName() + " " + cr.getTargetSurname();
        events.publishEvent(new FeedbackCompletedNotificationEvent(
            candidateEmail,
            candidateName,
            refereeName,
            company
        ));
    }

    private String lookupRelationshipCode(int relationshipId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT code FROM relationship_types WHERE id = ?", String.class, relationshipId
            );
        } catch (Exception e) {
            return "OTHER";
        }
    }

    // ── Cross-module reads via JDBC ─────────────────────────

    private String lookupUserName(UUID userId) {
        try {
            String fullName = jdbcTemplate.queryForObject(
                "SELECT CONCAT(name, ' ', surname) FROM user_profiles WHERE user_id = ?", String.class, userId
            );
            if (fullName != null && !fullName.isBlank()) {
                return fullName.trim();
            }
        } catch (Exception ignored) {}

        try {
            return jdbcTemplate.queryForObject(
                "SELECT email FROM users WHERE id = ?", String.class, userId
            );
        } catch (Exception e) {
            return "Profesional";
        }
    }

    private String lookupUserEmail(UUID userId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT email FROM users WHERE id = ?", String.class, userId
            );
        } catch (Exception e) {
            return "";
        }
    }

    private String lookupExperienceCompanyName(UUID experienceId) {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT company_name FROM experiences WHERE id = ?", String.class, experienceId
            );
        } catch (Exception e) {
            return "Empresa";
        }
    }

    // ── Toggle Visibility ───────────────────────────────────

    @Transactional
    public CacheRequestViewDTO toggleVisibility(UUID userId, UUID cacheRequestId, boolean visible) {
        CacheRequest cr = cacheRequestRepository.findById(cacheRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!cr.getUserId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para modificar esta solicitud");
        }

        cr.setVisible(visible);
        CacheRequest saved = cacheRequestRepository.saveAndFlush(cr);

        // If the request was finished, we MUST trigger recalculation of metrics!
        if (cr.isFinished()) {
            events.publishEvent(new com.ia.root.backend.feedback.FeedbackCompletedEvent(cr.getId(), cr.getUserId(), cr.getExperienceId()));
        }

        return toViewDTO(saved);
    }

    // ── Mapping ─────────────────────────────────────────────

    private CacheRequestViewDTO toViewDTO(CacheRequest cr) {
        return new CacheRequestViewDTO(
            cr.getId(), cr.getExperienceId(), cr.getRelationshipId(),
            cr.isStillWorksThere(), cr.getTargetName(), cr.getTargetSurname(),
            cr.getTargetEmail(), cr.getTargetPhone(), cr.isFinished(), cr.isVisible(),
            cr.getTrustScore(), cr.getTrustLevel(), cr.getCreatedAt(), cr.getExtraAnswers()
        );
    }
}
