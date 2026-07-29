package com.ia.root.backend.feedback;

import com.ia.root.backend.auth.internal.infrastructure.security.SecurityUser;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.CreateCacheRequestDTO;
import com.ia.root.backend.feedback.internal.infrastructure.web.dto.SubmitQuestionnaireDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

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

@ActiveProfiles("test")

@Transactional
public class FeedbackIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper jsonMapper;

    private UUID userId;
    private UUID experienceId;
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        jdbcTemplate.update(
            "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
            userId, "Test User", "user@example.com", "hash", true, "LOCAL", "ROLE_USER"
        );

        experienceId = UUID.randomUUID();

        jdbcTemplate.update(
            "INSERT INTO experiences (id, user_id, company_name, position, start_date) VALUES (?, ?, ?, ?, CURRENT_DATE)",
            experienceId, userId, "Acme Corp", "Developer"
        );

        securityUser = new SecurityUser(userId, "user@example.com", "hash", "ROLE_USER");
    }

    private String createCacheRequestAndGetUrlToken(String name, String surname, String email) throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, name, surname, email, "+34600000000"
        );

        MvcResult result = mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        return jsonMapper.readTree(response).get("urlToken").asText();
    }

    private void createCacheRequest(String name, String surname, String email) throws Exception {
        createCacheRequestAndGetUrlToken(name, surname, email);
    }

    @Test
    void shouldCreateCacheRequest() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Ana", "López", "ana@ref.com", "+34600000000"
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetName").value("Ana"))
                .andExpect(jsonPath("$.targetSurname").value("López"))
                .andExpect(jsonPath("$.targetEmail").value("ana@ref.com"))
                .andExpect(jsonPath("$.finished").value(false));
    }

    @Test
    void shouldRejectCacheRequestWithInvalidEmail() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Ana", "López", "invalid-email", "+34600000000"
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
            null, 0, true, "", "", "ana@ref.com", null
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectSelfRequest() throws Exception {
        CreateCacheRequestDTO dto = new CreateCacheRequestDTO(
            experienceId, 0, true, "Self", "User", "user@example.com", "+34600000000"
        );

        mockMvc.perform(post("/api/feedback/requests")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

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
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldGetQuestionnaireByUrlToken() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidateName").exists())
                .andExpect(jsonPath("$.questions.length()").value(5));
    }

    @Test
    void shouldCompleteFullFeedbackFlow() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();

        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();

        var tree = mapper.readTree(questionnaireJson);

        List<SubmitQuestionnaireDTO.BehavioralAnswer> answers = new java.util.ArrayList<>();

        for (var qNode : tree.get("questions")) {
            UUID questionId = UUID.fromString(qNode.get("id").asText());

            var firstOptId = UUID.fromString(qNode.get("options").get(0).get("id").asText());

            answers.add(new SubmitQuestionnaireDTO.BehavioralAnswer(questionId, List.of(firstOptId)));
        }

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(answers, "Es un gran profesional", Map.of());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/requests")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].finished").value(true));
    }

    @Test
    void shouldRejectSubmittingSameQuestionnaireTwice() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();

        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();

        var tree = mapper.readTree(questionnaireJson);

        List<SubmitQuestionnaireDTO.BehavioralAnswer> answers = new java.util.ArrayList<>();

        for (var qNode : tree.get("questions")) {
            UUID questionId = UUID.fromString(qNode.get("id").asText());

            var firstOptId = UUID.fromString(qNode.get("options").get(0).get("id").asText());

            answers.add(new SubmitQuestionnaireDTO.BehavioralAnswer(questionId, List.of(firstOptId)));
        }

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(answers, null, Map.of());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectQuestionnaireWithEmptyAnswers() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        SubmitQuestionnaireDTO emptyDto = new SubmitQuestionnaireDTO(List.of(), null, null);

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldToggleRequestVisibility() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Juan", "Gómez", "juan@ref.com");

        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        UUID qId = jdbcTemplate.queryForObject("SELECT id FROM behavioral_questions LIMIT 1", UUID.class);
        UUID optId = jdbcTemplate.queryForObject("SELECT id FROM behavioral_question_options WHERE question_id = ? LIMIT 1", UUID.class, qId);

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(
            List.of(new SubmitQuestionnaireDTO.BehavioralAnswer(qId, List.of(optId))),
            "OK", Map.of()
        );

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(false));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch("/api/feedback/requests/" + requestId + "/visibility")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"visible\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visible").value(true));
    }

    @Test
    void shouldCalculateTrustScoreAndExposeExperienceMetrics() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Jefe", "Boss", "boss@acme.com");

        UUID registeredRefererId = UUID.randomUUID();

        jdbcTemplate.update(
            "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
            registeredRefererId, "Jefe Boss", "boss@acme.com", "hash", true, "LOCAL", "ROLE_USER"
        );

        MvcResult questionnaireResult = mockMvc.perform(get("/api/questionnaire/" + urlToken))
                .andExpect(status().isOk())
                .andReturn();

        String questionnaireJson = questionnaireResult.getResponse().getContentAsString();

        tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();

        var tree = mapper.readTree(questionnaireJson);

        List<SubmitQuestionnaireDTO.BehavioralAnswer> answers = new java.util.ArrayList<>();

        for (var qNode : tree.get("questions")) {
            UUID questionId = UUID.fromString(qNode.get("id").asText());

            var firstOptId = UUID.fromString(qNode.get("options").get(0).get("id").asText());

            answers.add(new SubmitQuestionnaireDTO.BehavioralAnswer(questionId, List.of(firstOptId)));
        }

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(answers, "Excelente", Map.of());

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/feedback/requests")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trustScore").value(90))
                .andExpect(jsonPath("$[0].trustLevel").value("EXCELENTE"));
    }

    @Test
    void shouldDeletePendingFeedbackRequest() throws Exception {
        String urlToken = createCacheRequestAndGetUrlToken("Ana", "López", "ana@ref.com");

        UUID requestId = jdbcTemplate.queryForObject(
            "SELECT id FROM cache_requests WHERE url_token = ?",
            UUID.class, urlToken
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/feedback/requests/" + requestId)
                .with(user(securityUser)))
                .andExpect(status().isNoContent());

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

        UUID qId = jdbcTemplate.queryForObject("SELECT id FROM behavioral_questions LIMIT 1", UUID.class);

        UUID optId = jdbcTemplate.queryForObject("SELECT id FROM behavioral_question_options WHERE question_id = ? LIMIT 1", UUID.class, qId);

        SubmitQuestionnaireDTO submitDto = new SubmitQuestionnaireDTO(
            List.of(new SubmitQuestionnaireDTO.BehavioralAnswer(qId, List.of(optId))),
            "OK", Map.of()
        );

        mockMvc.perform(post("/api/questionnaire/" + urlToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(submitDto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/feedback/requests/" + requestId + "/remind")
                .with(user(securityUser)))
                .andExpect(status().isBadRequest());
    }
}
