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
        // Create an initial profile for the registered user
        UserProfile profile = new UserProfile(event.userId(), event.name(), event.email());
        userProfileRepository.save(profile);
    }
}
