package com.ia.root.backend.auth.internal.application;
import com.ia.root.backend.auth.internal.domain.model.*;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;
import com.ia.root.backend.auth.internal.domain.repository.*;
import com.ia.root.backend.auth.internal.infrastructure.security.JwtProvider;

import com.ia.root.backend.auth.UserRequiresOtpEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher events;
    private final long refreshExpirationMs;

    public AuthService(UserRepository userRepository,
                       UserOtpRepository userOtpRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       ApplicationEventPublisher events,
                       @Value("${app.jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.events = events;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        String requestedRole = request.role() != null ? request.role().toUpperCase() : "ROLE_USER";
        if (!requestedRole.equals("ROLE_USER") && !requestedRole.equals("ROLE_COMPANY")) {
            throw new IllegalArgumentException("Rol no válido. Debe ser ROLE_USER o ROLE_COMPANY");
        }

        User user = User.createLocal(
            request.name(),
            request.email(),
            passwordEncoder.encode(request.password()),
            requestedRole
        );

        User savedUser = userRepository.save(user);

        // Generate OTP
        String otp = generateOtp();
        UserOtp userOtp = new UserOtp(otp, savedUser, ZonedDateTime.now().plusMinutes(15));
        userOtpRepository.save(userOtp);

        // Publish Domain Event for decoupled email sending
        events.publishEvent(new UserRequiresOtpEvent(savedUser.getEmail(), otp, UserRequiresOtpEvent.OtpPurpose.ACCOUNT_VERIFICATION));
        
        // Publish Domain Event for cross-module profile creation
        events.publishEvent(new com.ia.root.backend.auth.UserRegisteredEvent(
            savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole()
        ));
    }

    @Transactional
    public void confirmAccount(OtpVerificationRequest request) {
        UserOtp userOtp = userOtpRepository.findByCodeAndUser_Email(request.code(), request.email())
                .orElseThrow(() -> new IllegalArgumentException("Código OTP inválido o usuario incorrecto"));

        if (userOtp.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("El código OTP ha expirado");
        }

        User user = userOtp.getUser();
        user.activate();
        userRepository.save(user);

        // Delete all OTPs for this user since account is now verified
        userOtpRepository.deleteByUser_Id(user.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("La cuenta no está activada. Por favor verifica tu email.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String jwt = jwtProvider.generateToken(authentication);
        String refreshToken = createRefreshToken(user);

        return new AuthResponse(jwt, refreshToken, user.getId(), user.getName(), user.getRole());
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un usuario con ese email"));

        if (!user.isActive()) {
            throw new IllegalArgumentException("La cuenta no está activada. Por favor verifica tu email primero.");
        }

        // Delete any existing OTPs for this user before creating a new one
        userOtpRepository.deleteByUser_Id(user.getId());

        String otp = generateOtp();
        UserOtp userOtp = new UserOtp(otp, user, ZonedDateTime.now().plusMinutes(15));
        userOtpRepository.save(userOtp);

        events.publishEvent(new UserRequiresOtpEvent(user.getEmail(), otp, UserRequiresOtpEvent.OtpPurpose.PASSWORD_RESET));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        UserOtp userOtp = userOtpRepository.findByCodeAndUser_Email(request.code(), request.email())
                .orElseThrow(() -> new IllegalArgumentException("Código OTP inválido o usuario incorrecto"));

        if (userOtp.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("El código OTP ha expirado");
        }

        User user = userOtp.getUser();
        user.updatePassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        userOtpRepository.deleteByUser_Id(user.getId());
    }

    @Transactional
    public String createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser_Id(user.getId());

        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
            token,
            user,
            ZonedDateTime.now().plus(java.time.Duration.ofMillis(refreshExpirationMs))
        );
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.refreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token no encontrado"));

        if (refreshToken.isRevoked()) {
            refreshTokenRepository.deleteByUser_Id(refreshToken.getUser().getId());
            throw new IllegalArgumentException("Este token ha sido revocado. Posible brecha de seguridad. Inicie sesión de nuevo.");
        }

        if (refreshToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("El refresh token ha expirado. Inicie sesión de nuevo.");
        }

        User user = refreshToken.getUser();

        // Complete rotation: revoke/delete the old refresh token
        refreshTokenRepository.delete(refreshToken);

        // Generate a new Access Token and a new Refresh Token
        String accessToken = jwtProvider.generateTokenFromEmail(user.getEmail());
        String newRefreshToken = createRefreshToken(user);

        return new TokenRefreshResponse(accessToken, newRefreshToken);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
