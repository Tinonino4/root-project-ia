package com.ia.root.backend.professional.internal.application;

import com.ia.root.backend.auth.UserRegisteredEvent;
import com.ia.root.backend.professional.internal.domain.model.UserProfile;
import com.ia.root.backend.professional.internal.domain.repository.UserProfileRepository;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredListener {

    private final UserProfileRepository userProfileRepository;

    public UserRegisteredListener(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        // Create an initial profile for the registered user with a clean, unique username
        String baseUsername = slugify(event.name());
        String username = baseUsername;
        int count = 1;
        while (userProfileRepository.existsByUsername(username)) {
            username = baseUsername + "-" + count;
            count++;
        }

        UserProfile profile = new UserProfile(event.userId(), event.name(), event.email(), username);
        userProfileRepository.save(profile);
    }

    private String slugify(String name) {
        if (name == null || name.isBlank()) {
            return "cacher";
        }
        // Normalize name: lowercase, replace spaces and special chars with a dash, collapse multiple dashes
        String slug = name.toLowerCase()
            .replaceAll("[^a-z0-9-_]", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            return "cacher";
        }
        return slug;
    }
}
