package com.ia.root.backend.auth;

import com.ia.root.backend.TestcontainersConfiguration;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.ForgotPasswordRequest;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.LoginRequest;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.OtpVerificationRequest;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.RegisterRequest;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.ResetPasswordRequest;
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
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    }

    // ------------------------------------------------------------------ 
    // Auth flow: Register → Confirm → Login
    // ------------------------------------------------------------------ 

    @Test
    void shouldRegisterConfirmAndLogin() throws Exception {
        // 1. Register
        RegisterRequest registerRequest = new RegisterRequest(
                "Test User", "test@example.com", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // 2. Get OTP code directly from DB (avoids LazyInitializationException on UserOtp.user)
        String otpCode = jdbcTemplate.queryForObject(
                "SELECT code FROM user_otps WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                String.class, "test@example.com"
        );

        // 3. Confirm Account
        OtpVerificationRequest confirmRequest = new OtpVerificationRequest(
                "test@example.com", otpCode
        );

        mockMvc.perform(post("/api/auth/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(confirmRequest)))
                .andExpect(status().isOk());

        // 4. Login
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    // ------------------------------------------------------------------ 
    // Registration validation
    // ------------------------------------------------------------------ 

    @Test
    void shouldRejectDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "User One", "duplicate@example.com", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Same email again
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Bad Email", "not-an-email", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectShortPassword() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Short Pass", "short@example.com", "12345", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "", "blank@example.com", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // OTP validation
    // ------------------------------------------------------------------ 

    @Test
    void shouldRejectInvalidOtpCode() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Otp Test", "otp@example.com", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        OtpVerificationRequest badOtp = new OtpVerificationRequest("otp@example.com", "000000");

        mockMvc.perform(post("/api/auth/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(badOtp)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Login validation
    // ------------------------------------------------------------------ 

    @Test
    void shouldRejectLoginForUnconfirmedAccount() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "Unconfirmed", "unconfirmed@example.com", "password123", "ROLE_USER"
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("unconfirmed@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectLoginWithWrongPassword() throws Exception {
        // Register + confirm
        registerAndConfirmUser("wrongpass@example.com", "correctpass", "Wrong Pass");

        LoginRequest loginRequest = new LoginRequest("wrongpass@example.com", "incorrectpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectLoginForNonExistentUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest("ghost@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Reset Password
    // ------------------------------------------------------------------ 

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        registerAndConfirmUser("reset@example.com", "oldPassword1", "Reset User");

        // 1. Request password reset
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("reset@example.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 2. Get OTP from DB
        String otpCode = jdbcTemplate.queryForObject(
                "SELECT code FROM user_otps WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                String.class, "reset@example.com"
        );

        // 3. Reset password
        ResetPasswordRequest resetRequest = new ResetPasswordRequest("reset@example.com", otpCode, "newPassword1");
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        // 4. Login with new password
        LoginRequest loginRequest = new LoginRequest("reset@example.com", "newPassword1");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());

        // 5. Old password no longer works
        LoginRequest oldLoginRequest = new LoginRequest("reset@example.com", "oldPassword1");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(oldLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectResetWithInvalidOtp() throws Exception {
        registerAndConfirmUser("badreset@example.com", "password123", "Bad Reset");

        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("badreset@example.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk());

        ResetPasswordRequest resetRequest = new ResetPasswordRequest("badreset@example.com", "000000", "newPassword1");
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectResetWithExpiredOtp() throws Exception {
        registerAndConfirmUser("expired@example.com", "password123", "Expired OTP");

        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("expired@example.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk());

        // Expire the OTP directly in DB
        jdbcTemplate.update(
                "UPDATE user_otps SET expires_at = NOW() - INTERVAL '1 hour' WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                "expired@example.com"
        );

        String otpCode = jdbcTemplate.queryForObject(
                "SELECT code FROM user_otps WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                String.class, "expired@example.com"
        );

        ResetPasswordRequest resetRequest = new ResetPasswordRequest("expired@example.com", otpCode, "newPassword1");
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectForgotPasswordForNonExistentUser() throws Exception {
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("nobody@example.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectForgotPasswordForUnconfirmedAccount() throws Exception {
        RegisterRequest reg = new RegisterRequest("Unconfirmed", "unconfirmed-reset@example.com", "password123", "ROLE_USER");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest("unconfirmed-reset@example.com");
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectResetPasswordWithShortPassword() throws Exception {
        ResetPasswordRequest resetRequest = new ResetPasswordRequest("any@example.com", "123456", "short");
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ 
    // Helper
    // ------------------------------------------------------------------ 

    private void registerAndConfirmUser(String email, String password, String name) throws Exception {
        RegisterRequest reg = new RegisterRequest(name, email, password, "ROLE_USER");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        String otpCode = jdbcTemplate.queryForObject(
                "SELECT code FROM user_otps WHERE user_id = (SELECT id FROM users WHERE email = ?)",
                String.class, email
        );

        OtpVerificationRequest confirm = new OtpVerificationRequest(email, otpCode);
        mockMvc.perform(post("/api/auth/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(confirm)))
                .andExpect(status().isOk());
    }
}
