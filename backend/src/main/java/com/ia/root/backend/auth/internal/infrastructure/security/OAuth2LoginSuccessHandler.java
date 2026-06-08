package com.ia.root.backend.auth.internal.infrastructure.security;

import com.ia.root.backend.auth.internal.domain.model.User;
import com.ia.root.backend.auth.internal.domain.repository.UserRepository;
import com.ia.root.backend.auth.internal.application.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final org.springframework.context.ApplicationEventPublisher events;
    private final OAuth2AuthorizedClientService authorizedClientService;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(JwtProvider jwtProvider, 
                                     UserRepository userRepository, 
                                     AuthService authService,
                                     org.springframework.context.ApplicationEventPublisher events,
                                     OAuth2AuthorizedClientService authorizedClientService) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.authService = authService;
        this.events = events;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub"); // typical for OIDC
        if (providerId == null) {
            providerId = oAuth2User.getName();
        }

        if (email == null && "GITHUB".equals(provider)) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
            );
            if (client != null && client.getAccessToken() != null) {
                String accessToken = client.getAccessToken().getTokenValue();
                email = fetchGitHubEmail(accessToken);
            }
        }

        if (email == null) {
            throw new ServletException("OAuth2 provider didn't return an email");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            if ("LOCAL".equals(user.getProvider())) {
                user.linkOAuth2Provider(provider, providerId);
                user.activate();
                userRepository.save(user);
            }
        } else {
            user = User.createFromOAuth2(
                name != null ? name : email,
                email,
                provider,
                providerId
            );
            userRepository.save(user);
            isNewUser = true;
        }

        if (isNewUser) {
            events.publishEvent(new com.ia.root.backend.auth.UserRegisteredEvent(
                user.getId(), user.getName(), user.getEmail(), user.getRole()
            ));
        }

        // Generate JWT token and Refresh Token
        String token = jwtProvider.generateTokenFromEmail(email); 
        String refreshToken = authService.createRefreshToken(user);
        
        String targetUrl = frontendUrl + "/oauth2/redirect?token=" + token + "&refreshToken=" + refreshToken;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String fetchGitHubEmail(String accessToken) {
        try {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI.create("https://api.github.com/user/emails"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();
            
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(response.body());
                if (root.isArray()) {
                    String primaryEmail = null;
                    for (com.fasterxml.jackson.databind.JsonNode node : root) {
                        if (node.has("email")) {
                            String emailVal = node.get("email").asText();
                            boolean primary = node.has("primary") && node.get("primary").asBoolean();
                            boolean verified = node.has("verified") && node.get("verified").asBoolean();
                            if (primary && verified) {
                                return emailVal;
                            }
                            if (primaryEmail == null) {
                                primaryEmail = emailVal;
                            }
                        }
                    }
                    if (primaryEmail != null) {
                        return primaryEmail;
                    }
                }
            }
        } catch (Exception e) {
            // Log warning/error or ignore
        }
        return null;
    }
}
