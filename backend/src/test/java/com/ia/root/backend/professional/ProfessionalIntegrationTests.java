package com.ia.root.backend.professional;

import com.ia.root.backend.TestcontainersConfiguration;
import com.ia.root.backend.auth.internal.infrastructure.security.SecurityUser;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.domain.repository.ExperienceRepository;
import com.ia.root.backend.professional.internal.domain.repository.UserProfileRepository;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.ExperienceRequest;
import com.ia.root.backend.professional.internal.infrastructure.web.dto.UserProfileRequest;
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
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class ProfessionalIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    private UUID userId;
    private SecurityUser securityUser;

    @BeforeEach
    void setUp() {
        // Clean in FK-safe order
        jdbcTemplate.execute("DELETE FROM feedback_responses");
        jdbcTemplate.execute("DELETE FROM cache_requests");
        jdbcTemplate.execute("DELETE FROM user_skills_metrics");
        experienceRepository.deleteAll();
        userProfileRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM user_otps");
        jdbcTemplate.execute("DELETE FROM users");

        // Insert user via JDBC to avoid importing auth internal domain classes
        userId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
                userId, "Pro User", "pro@example.com", "irrelevant", true, "LOCAL", "ROLE_USER"
        );

        securityUser = new SecurityUser(userId, "pro@example.com", "irrelevant", "ROLE_USER");

        // Pre-create profile (normally done by UserRegisteredListener event)
        UserProfile profile = new UserProfile(userId, "Pro", "pro@example.com");
        profile.updateSurname("User");
        userProfileRepository.save(profile);
    }

    // ------------------------------------------------------------------ 
    // Profile: GET & PUT
    // ------------------------------------------------------------------ 

    @Test
    void shouldGetProfile() throws Exception {
        mockMvc.perform(get("/api/v1/professional/profile")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pro"))
                .andExpect(jsonPath("$.surname").value("User"))
                .andExpect(jsonPath("$.contactEmail").value("pro@example.com"));
    }

    @Test
    void shouldUpdateProfile() throws Exception {
        UserProfileRequest updateRequest = new UserProfileRequest(
                "Updated", "Name", null, "New bio", "Madrid",
                null, "28001", null, null, "Developer", null
        );

        mockMvc.perform(put("/api/v1/professional/profile")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.surname").value("Name"))
                .andExpect(jsonPath("$.aboutMe").value("New bio"))
                .andExpect(jsonPath("$.city").value("Madrid"))
                .andExpect(jsonPath("$.zipcode").value("28001"))
                .andExpect(jsonPath("$.jobTitle").value("Developer"));
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/professional/profile"))
                .andExpect(status().isUnauthorized());
    }

    // ------------------------------------------------------------------ 
    // Experience: CRUD completo
    // ------------------------------------------------------------------ 

    @Test
    void shouldAddExperience() throws Exception {
        ExperienceRequest request = new ExperienceRequest(
                "Google", "Cloud", "Engineer",
                LocalDate.of(2020, 1, 1), null, "Building stuff"
        );

        mockMvc.perform(post("/api/v1/professional/experiences")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Google"))
                .andExpect(jsonPath("$.department").value("Cloud"))
                .andExpect(jsonPath("$.position").value("Engineer"))
                .andExpect(jsonPath("$.functions").value("Building stuff"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void shouldListExperiences() throws Exception {
        // Add two experiences
        createExperience("Google", "Engineer", LocalDate.of(2020, 1, 1), null);
        createExperience("Meta", "Senior Engineer", LocalDate.of(2022, 6, 1), null);

        mockMvc.perform(get("/api/v1/professional/experiences")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateExperience() throws Exception {
        // Create experience and extract its ID
        String expId = createExperienceAndGetId("Google", "Engineer", LocalDate.of(2020, 1, 1));

        ExperienceRequest updateRequest = new ExperienceRequest(
                "Google", "Cloud", "Senior Engineer",
                LocalDate.of(2020, 1, 1), LocalDate.of(2023, 12, 31), "Leading team"
        );

        mockMvc.perform(put("/api/v1/professional/experiences/" + expId)
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.position").value("Senior Engineer"))
                .andExpect(jsonPath("$.department").value("Cloud"))
                .andExpect(jsonPath("$.functions").value("Leading team"));
    }

    @Test
    void shouldDeleteExperience() throws Exception {
        String expId = createExperienceAndGetId("Temporal Corp", "Intern", LocalDate.of(2019, 1, 1));

        mockMvc.perform(delete("/api/v1/professional/experiences/" + expId)
                .with(user(securityUser)))
                .andExpect(status().isNoContent());

        // Verify it's gone
        mockMvc.perform(get("/api/v1/professional/experiences")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ------------------------------------------------------------------ 
    // Experience: validation & authorization
    // ------------------------------------------------------------------ 

    @Test
    void shouldRejectExperienceWithBlankCompanyName() throws Exception {
        ExperienceRequest request = new ExperienceRequest(
                "", null, "Engineer", LocalDate.of(2020, 1, 1), null, null
        );

        mockMvc.perform(post("/api/v1/professional/experiences")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectExperienceWithNullStartDate() throws Exception {
        ExperienceRequest request = new ExperienceRequest(
                "Company", null, "Engineer", null, null, null
        );

        mockMvc.perform(post("/api/v1/professional/experiences")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotAllowDeletingOtherUsersExperience() throws Exception {
        // Create experience as our user
        String expId = createExperienceAndGetId("My Company", "Dev", LocalDate.of(2020, 1, 1));

        // Create a different user
        UUID otherUserId = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO users (id, name, email, password_hash, is_active, provider, role) VALUES (?, ?, ?, ?, ?, ?, ?)",
                otherUserId, "Other", "other@example.com", "x", true, "LOCAL", "ROLE_USER"
        );
        SecurityUser otherUser = new SecurityUser(otherUserId, "other@example.com", "x", "ROLE_USER");

        // Try to delete as different user
        mockMvc.perform(delete("/api/v1/professional/experiences/" + expId)
                .with(user(otherUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyExperiencesList() throws Exception {
        mockMvc.perform(get("/api/v1/professional/experiences")
                .with(user(securityUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ------------------------------------------------------------------ 
    // Helpers
    // ------------------------------------------------------------------ 

    private void createExperience(String company, String position, LocalDate start, LocalDate end) throws Exception {
        ExperienceRequest request = new ExperienceRequest(company, null, position, start, end, null);
        mockMvc.perform(post("/api/v1/professional/experiences")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private String createExperienceAndGetId(String company, String position, LocalDate start) throws Exception {
        ExperienceRequest request = new ExperienceRequest(company, null, position, start, null, null);
        MvcResult result = mockMvc.perform(post("/api/v1/professional/experiences")
                .with(user(securityUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        // Parse id from response using Jackson 3.x
        return tools.jackson.databind.json.JsonMapper.builder().build()
                .readTree(result.getResponse().getContentAsString())
                .get("id").asText();
    }
}
