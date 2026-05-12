package com.ia.root.backend.auth.internal.infrastructure.web;
import com.ia.root.backend.auth.internal.infrastructure.web.dto.*;
import com.ia.root.backend.auth.internal.application.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(summary = "Registrar un nuevo usuario",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado. Se envía OTP por email"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado", content = @Content)
        })
    public ResponseEntity<MessageResponse> register(@RequestBody @Valid RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponse("Usuario registrado. Por favor revisa tu correo para el código de confirmación."));
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirmar cuenta usando código OTP",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cuenta activada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Código OTP inválido o expirado", content = @Content)
        })
    public ResponseEntity<MessageResponse> confirmAccount(@RequestBody @Valid OtpVerificationRequest request) {
        authService.confirmAccount(request);
        return ResponseEntity.ok(new MessageResponse("Cuenta activada exitosamente. Ya puedes iniciar sesión."));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión y obtener token JWT",
        responses = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve JWT token",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas o cuenta no activada", content = @Content)
        })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar restablecimiento de contraseña (envía código OTP por email)",
        responses = {
            @ApiResponse(responseCode = "200", description = "Código OTP enviado al email"),
            @ApiResponse(responseCode = "400", description = "Email no encontrado o cuenta no activada", content = @Content)
        })
    public ResponseEntity<MessageResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.requestPasswordReset(request);
        return ResponseEntity.ok(new MessageResponse("Se ha enviado un código de verificación a tu email."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña usando código OTP",
        responses = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Código OTP inválido, expirado o contraseña débil", content = @Content)
        })
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada exitosamente."));
    }
}
