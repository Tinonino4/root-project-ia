package com.ia.root.backend.auth.internal.infrastructure.web;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;
import com.ia.root.backend.auth.internal.application.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth", version = "1")
@Tag(name = "Authentication", description = "Endpoints para registro, confirmación y login de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario")
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuario registrado. Por favor revisa tu correo para el código de confirmación."));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar cuenta usando código OTP")
    public ResponseEntity<MessageResponse> confirmAccount(@RequestBody @Valid OtpVerificationRequest request) {
        authService.confirmAccount(request);
        return ResponseEntity.ok(new MessageResponse("Cuenta activada exitosamente. Ya puedes iniciar sesión."));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar restablecimiento de contraseña (envía código OTP por email)")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(new MessageResponse("Se ha enviado un código de verificación a tu email."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña usando código OTP")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente."));
    }
}
