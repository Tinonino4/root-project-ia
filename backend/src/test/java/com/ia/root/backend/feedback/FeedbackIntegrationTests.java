package com.ia.root.backend.feedback;

import com.ia.root.backend.TestcontainersConfiguration;
import com.ia.root.backend.auth.internal.infrastructure.security.SecurityUser;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CreateCacheRequestDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.SubmitQuestionnaireDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class FeedbackIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID userId;
    private UUID experienceId;
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        // Clean in FK-safe order
        jdbcTemplate.execute("DELETE FROM feedback_responses");
        jdbcTemplate.execute("DELETE FROM cache_requests");
        jdbcTemplate.execute("DELETE FROM user_skills_metrics");
        jdbcTemplate.execute("DELETE FROM experiences");
        jdbcTemplate.execute("DELETE FROM user_profiles");
        jdbcTemplate.execute("DELETE FROM user_otps");
        jdbcTemplate.execute("DELETE FROM users");

        // Create test user
        userId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
            userId, "Test User", "test@example.com", "irrelevant", true, "LOCAL", "ROLE_USER"
        );

        // Create experience
        experienceId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO experiences (id, user_id, company_name, position, start_date) VALUES (?, ?, ?, ?, ?)",
            experienceId, userId, "Acme Corp", "Developer", LocalDate.of(2020, 1, 1)
        );

        // Create user profile
        jdbcTemplate.update(
            "INSERT INTO user_profiles (id, user_id, name, contact_email) VALUES (?, ?, ?, ?)",
            UUID.randomUUID(), userId, "Test User", "test@example.com"
        );

        // Initialize skills metrics
        jdbcTemplate.update(
            "INSERT INTO user_skills_metrics (id, user_id, teamwork, self_confidence, proactivity, integrity, flexibility, average_score) VALUES (?, ?, 0, 0, 0, 0, 0, 0)",
            UUID.randomUUID(), userId
        );

        securityUser = new SecurityUser(userId, "test@example.com", "irrelevant", "ROLE_USER");
    }

    // ------------------------------------------------------------------ 
    // Catálogo de categorías y preguntas
    // ------------------------------------------------------------------ 

    @Test
    void shouldGetSkillCategories() throws Exception {
        mockMvc.perform(get("/api/feedback/categories")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].code").value("TEAMWORK"))
                .andExpect(jsonPath("$[0].questions.length()").value(5))
                .andExpect(jsonPath("$[1].code").value("PROACTIVITY"))
                .andExpect(jsonPath("$[2].code").value("INTEGRITY"))
                .andExpect(jsonPath("$[3].code").value("SELF_CONFIDENCE"))
                .andExpect(jsonPath("$[4].code").value("FLEXIBILITY"));
    }

    // ------------------------------------------------------------------ 
    // Catálogo de tipos de relación
    // ------------------------------------------------------------------ 

    @Test
    void shouldGetRelationshipTypes() throws Exception {
        mockMvc.perform(get("/api/feedback/relationships")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].code").value("DIRECT_MANAGER"))
                .andExpect(jsonPath("$[1].code").value("COLLEAGUE"))
                .andExpect(jsonPath("$[2].code").value("SUBORDINATE"))
                .andExpect(jsonPath("$[3].code").value("CLIENT"))
                .andExpect(jsonPath("$[4].code").value("OTHER"));
    }

    // ------------------------------------------------------------------ 
    // Crear solicitud de feedback
    // ------------------------------------------------------------------ 

    @Test
    void shouldCreateCacheRequest() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Juan", "García", "juan@ref.com", "+34600000000"
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.experienceId").value(experienceId.toString()))
                .andExpect(jsonPath("$.targetName").value("Juan"))
                .andExpect(jsonPath("$.targetSurname").value("García"))
                .andExpect(jsonPath("$.targetEmail").value("juan@ref.com"))
                .andExpect(jsonPath("$.finished").value(false));
    }

    @Test
    void shouldRejectCacheRequestWithInvalidEmail() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Juan", "García", "not-an-email", null
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCacheRequestWithMissingFields() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, null, true, "", "", "juan@ref.com", null
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectSelfReferencingRequest() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Self", "Reference", "test@example.com", null
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Listar solicitudes
    // ------------------------------------------------------------------ 

    @Test
    void shouldListCacheRequests() throws Exception {
        createCacheRequest("Ana", "López", "ana@ref.com");
        createCacheRequest("Pedro", "Martín", "pedro@ref.com");

        mockMvc.perform(get("/api/feedback/requests")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldListCacheRequestsByExperience() throws Exception {
        createCacheRequest("Ana", "López", "ana@ref.com");

        mockMvc.perform(get("/api/feedback/requests/experience/" + experienceId)
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].targetName").value("Ana"));
    }

    @Test
    void shouldReturnZeroCompletedCount() throws Exception {
        createCacheRequest("Ana", "López", "ana@ref.com");

        mockMvc.perform(get("/api/feedback/requests/experience/" + experienceId + "/count")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    // ------------------------------------------------------------------ 
    // Cuestionario público (sin auth)
    // ------------------------------------------------------------------ 

    @Test
    void shouldGetQuestionnaireByToken() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cacheRequestId").exists())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.experienceId").value(experienceId.toString()))
                .andExpect(jsonPath("$.categories.length()").value(5))
                .andExpect(jsonPath("$.categories[0].questions.length()").value(5));
    }

    @Test
    void shouldRejectQuestionnaireWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/questionnaire/invalid-token-12345"))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Flujo completo: crear request → llenar cuestionario → verificar
    // ------------------------------------------------------------------ 

    @Test
    void shouldCompleteFullFeedbackFlow() throws Exception {
        // 1. Create cache request
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        // 2. Load questionnaire to get question IDs
        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();
        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();
        var tree = mapper.readTree(questionnaireJson);

        // 3. Build skill answers (all 25 questions with rating 4)
        List<SubmitQuestionnaireDTO.SkillAnswer> skillAnswers = new java.util.ArrayList<>();
        for (var category : tree.get("categories")) {
            for (var question : category.get("questions")) {
                UUID questionId = UUID.fromString(question.get("id").asText());
                skillAnswers.add(new SubmitQuestionnaireDTO.SkillAnswer(questionId, 4));
            }
        }

        Map<String, Object> extraAnswers = Map.of(
            "question1", 3,
            "question1_1", "Desarrollador Senior",
            "question2", 2,
            "question4", "Excelente trabajo en equipo",
            "question5", "Mejorar comunicación escrita",
            "question6", 1,
            "comments", "Es un gran profesional y compañero"
        );

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(skillAnswers, extraAnswers);

        // 4. Submit questionnaire
        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 4b. Verify that the request now includes the qualitative comments (extraAnswers)
        mockMvc.perform(get("/api/feedback/requests")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].extraAnswers.comments").value("Es un gran profesional y compañero"));

        // 5. Verify the request is marked as finished
        mockMvc.perform(get("/api/feedback/requests/experience/" + experienceId + "/count")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1));

        // 6. Verify skills metrics are immediately calculated because the new reference is visible by default
        mockMvc.perform(get("/api/skills/metrics")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamwork").value(4.0))
                .andExpect(jsonPath("$.proactivity").value(4.0))
                .andExpect(jsonPath("$.integrity").value(4.0))
                .andExpect(jsonPath("$.selfConfidence").value(4.0))
                .andExpect(jsonPath("$.flexibility").value(4.0))
                .andExpect(jsonPath("$.averageScore").value(4.0));

        // 6b. Verify the public profile contains the certified testimonial
        mockMvc.perform(get("/api/public/profile/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.experienceMetrics[0].testimonials.length()").value(1))
                .andExpect(jsonPath("$.experienceMetrics[0].testimonials[0].comment").value("Es un gran profesional y compañero"))
                .andExpect(jsonPath("$.experienceMetrics[0].testimonials[0].relationshipCode").value("DIRECT_MANAGER"))
                .andExpect(jsonPath("$.experienceMetrics[0].testimonials[0].evaluatorName").value("Ana"))
                .andExpect(jsonPath("$.experienceMetrics[0].testimonials[0].evaluatorSurname").value("López"));

        // 7. Find cacheRequestId in DB and toggle visibility to false
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(false));

        // 8. Verify skills metrics are now recalculated and show 0.0
        mockMvc.perform(get("/api/skills/metrics")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamwork").value(0.0))
                .andExpect(jsonPath("$.proactivity").value(0.0))
                .andExpect(jsonPath("$.integrity").value(0.0))
                .andExpect(jsonPath("$.selfConfidence").value(0.0))
                .andExpect(jsonPath("$.flexibility").value(0.0))
                .andExpect(jsonPath("$.averageScore").value(0.0));

        // 9. Toggle visibility back to true and verify metrics return to 4.0
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(true));

        mockMvc.perform(get("/api/skills/metrics")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamwork").value(4.0))
                .andExpect(jsonPath("$.averageScore").value(4.0));
    }

    @Test
    void shouldRejectSubmittingSameQuestionnaireTwice() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        // Load and submit questionnaire
        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();
        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();
        var tree = mapper.readTree(questionnaireJson);

        List<SubmitQuestionnaireDTO.SkillAnswer> skillAnswers = new java.util.ArrayList<>();
        for (var category : tree.get("categories")) {
            for (var question : category.get("questions")) {
                UUID questionId = UUID.fromString(question.get("id").asText());
                skillAnswers.add(new SubmitQuestionnaireDTO.SkillAnswer(questionId, 3));
            }
        }

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(skillAnswers, Map.of());

        // First submit — OK
        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        // Second submit — should fail
        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectQuestionnaireWithEmptyAnswers() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        SubmitQuestionnaireDTO emptyDto = new SubmitQuestionnaireDTO(List.of(), null);

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectQuestionnaireWithInvalidRating() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        // Use a rating of 6 (out of range 1-5)
        List<SubmitQuestionnaireDTO.SkillAnswer> badAnswers = List.of(
            new SubmitQuestionnaireDTO.SkillAnswer(UUID.randomUUID(), 6)
        );

        SubmitQuestionnaireDTO dto = new SubmitQuestionnaireDTO(badAnswers, null);

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Skills metrics
    // ------------------------------------------------------------------ 

    @Test
    void shouldGetSkillsMetrics() throws Exception {
        mockMvc.perform(get("/api/skills/metrics")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamwork").value(0.0))
                .andExpect(jsonPath("$.averageScore").value(0.0));
    }

    @Test
    void shouldReturn401ForSkillsMetricsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/skills/metrics"))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------ 
    // Helpers
    // ------------------------------------------------------------------ 

    private void createCacheRequest(String name, String surname, String email) throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, name, surname, email, null
        );
        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    private String createCacheRequestAndGetUrlToken(String name, String surname, String email) throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, name, surname, email, null
        );
        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Get urlToken from DB
        return jdbcTemplate.queryForObject(
            "SELECT url_token FROM cache_requests WHERE user_id = ? AND target_email = ?",
            String.class, userId, email
        );
    }

    // ------------------------------------------------------------------ 
    // Visibility Toggling Tests
    // ------------------------------------------------------------------ 

    @Test
    void shouldToggleRequestVisibility() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Juan", "Gómez", "juan@ref.com");
        
        // Find cacheRequestId in DB
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        // Submit questionnaire first so it is marked finished
        SubmitQuestionnaireDTO.SkillAnswer ans = new SubmitQuestionnaireDTO.SkillAnswer(
            jdbcTemplate.queryForObject("SELECT id FROM skill_questions LIMIT 1", UUID.class), 4
        );
        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(List.of(ans), Map.of());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        // Toggle visibility to false (visible = false)
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(false));

        // Toggle visibility back to true (visible = true)
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(true));
    }

    @Test
    void shouldFailToToggleRequestVisibilityForOtherUser() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Pedro", "Gómez", "pedro@ref.com");
        
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        // Try to toggle visibility with a different user
        SecurityUser otherUser = new SecurityUser(UUID.randomUUID(), "other@example.com", "irrelevant", "ROLE_USER");

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(otherUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":false}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCalculateTrustScoreAndExposeExperienceMetrics() throws Exception {
        // 1. Crear solicitud de feedback con correo corporativo cuyo dominio coincide con "Acme Corp"
        String urlToken = createCacheRequestAndGetUrlToken("Jefe", "Boss", "boss@acme.com");

        // Registrar boss@acme.com como usuario para obtener los puntos de usuario registrado (+20)
        UUID registeredRefererId = UUID.randomUUID();
        jdbcTemplate.update(
            "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
            registeredRefererId, "Jefe Boss", "boss@acme.com", "hash", true, "LOCAL", "ROLE_USER"
        );

        // 2. Cargar cuestionario público para obtener las preguntas
        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();
        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();
        var tree = mapper.readTree(questionnaireJson);

        // 3. Responder con puntuación máxima (5)
        List<SubmitQuestionnaireDTO.SkillAnswer> skillAnswers = new java.util.ArrayList<>();
        for (var category : tree.get("categories")) {
            for (var question : category.get("questions")) {
                UUID questionId = UUID.fromString(question.get("id").asText());
                skillAnswers.add(new SubmitQuestionnaireDTO.SkillAnswer(questionId, 5));
            }
        }

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(skillAnswers, Map.of());

        // 4. Enviar respuestas
        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        // 5. Verificar que el cálculo de confianza es correcto:
        // - Email Corporativo (boss@acme.com): +30
        // - Dominio Coincidente con "Acme Corp": +40
        // - Referente Registrado: +20
        // - Teléfono no provisto: +0
        // Total = 90% (Nivel EXCELENTE)
        mockMvc.perform(get("/api/feedback/requests")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trustScore").value(90))
                .andExpect(jsonPath("$[0].trustLevel").value("EXCELENTE"));

        // 6. Hacer visible la referencia para recalculados
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":true}"))
                .andExpect(status().isOk());

        // 7. Verificar que el endpoint de perfil público expone la visualización desgranada
        mockMvc.perform(get("/api/public/profile/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReferencesCount").value(1))
                .andExpect(jsonPath("$.experienceMetrics.length()").value(1))
                .andExpect(jsonPath("$.experienceMetrics[0].experienceId").value(experienceId.toString()))
                .andExpect(jsonPath("$.experienceMetrics[0].averageScore").value(5.0))
                .andExpect(jsonPath("$.experienceMetrics[0].referencesCount").value(1))
                .andExpect(jsonPath("$.experienceMetrics[0].categoryAverages.TEAMWORK").value(5.0))
                .andExpect(jsonPath("$.experienceMetrics[0].averageTrustScore").value(90.0))
                .andExpect(jsonPath("$.experienceMetrics[0].relationshipCounts.DIRECT_MANAGER").value(1));
    }

    // ------------------------------------------------------------------ 
    // Pruebas de Eliminación y Recordatorios
    // ------------------------------------------------------------------ 

    @Test
    void shouldDeletePendingFeedbackRequest() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        // Delete request
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/feedback/requests/" + requestId)
                .with(user(securityUser)))
                .andExpect(status().isNoContent());

        // Verify request is deleted in DB
        Integer count = jdbcTemplate.queryForObject(
            "SELECT count(*) FROM cache_requests WHERE id = ?",
            Integer.class, requestId
        );
        org.junit.jupiter.api.Assertions.assertEquals(0, count);
    }

    @Test
    void shouldRemindFeedbackRequest() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        // Send reminder
        mockMvc.perform(post("/api/feedback/requests/" + requestId + "/remind")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Recordatorio enviado correctamente"));
    }

    @Test
    void shouldRejectRemindingFinishedFeedbackRequest() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");
        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        // Submit questionnaire first
        SubmitQuestionnaireDTO.SkillAnswer ans = new SubmitQuestionnaireDTO.SkillAnswer(
            jdbcTemplate.queryForObject("SELECT id FROM skill_questions LIMIT 1", UUID.class), 4
        );
        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(List.of(ans), Map.of());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        // Attempt to remind - should fail because it is finished
        mockMvc.perform(post("/api/feedback/requests/" + requestId + "/remind")
                .with(user(securityUser)))
                .andExpect(status().isBadRequest());
    }
}
