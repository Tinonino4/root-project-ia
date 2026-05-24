package com.ia.root.backend.auth.internal.application;
import com.ia.root.backend.auth.internal.domain.model.*;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;
import com.ia.root.backend.auth.internal.domain.repository.*;
import com.ia.root.backend.auth.internal.infrastructure.security.JwtProvider;

import com.ia.root.backend.auth.UserRequiresOtpEvent;
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

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserOtpRepository userOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher events;

    public AuthService(UserRepository userRepository,
                       UserOtpRepository userOtpRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider,
                       ApplicationEventPublisher events) {
        this.userRepository = userRepository;
        this.userOtpRepository = userOtpRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.events = events;
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

        return new AuthResponse(jwt, user.getId(), user.getName(), user.getRole());
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

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
