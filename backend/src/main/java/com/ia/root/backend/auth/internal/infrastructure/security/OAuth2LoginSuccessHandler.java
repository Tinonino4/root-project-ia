package com.ia.root.backend.auth.internal.infrastructure.security;

import com.ia.root.backend.auth.internal.domain.model.User;
import com.ia.root.backend.auth.internal.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    private final org.springframework.context.ApplicationEventPublisher events;
    
    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public OAuth2LoginSuccessHandler(JwtProvider jwtProvider, UserRepository userRepository, org.springframework.context.ApplicationEventPublisher events) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
        this.events = events;
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

        if (email == null) {
            throw new ServletException("OAuth2 provider didn't return an email");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        boolean isNewUser = false;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update provider if needed, or link account
            if ("LOCAL".equals(user.getProvider())) {
                user.setProvider(provider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            // Register new user
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email);
            user.setProvider(provider);
            user.setProviderId(providerId);
            user.setActive(true); // OAuth2 emails are considered verified
            user.setRole("ROLE_USER");
            userRepository.save(user);
            isNewUser = true;
        }

        if (isNewUser) {
            events.publishEvent(new com.ia.root.backend.auth.UserRegisteredEvent(
                user.getId(), user.getName(), user.getEmail(), user.getRole()
            ));
        }

        // Generate JWT token
        String token = jwtProvider.generateTokenFromEmail(email); 
        
        String targetUrl = frontendUrl + "/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
