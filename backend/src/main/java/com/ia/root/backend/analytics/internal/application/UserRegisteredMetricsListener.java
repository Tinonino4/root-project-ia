package com.ia.root.backend.analytics.internal.application;

import com.ia.root.backend.auth.UserRegisteredEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserRegisteredMetricsListener {

    private final SkillsMetricsService metricsService;

    public UserRegisteredMetricsListener(SkillsMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        metricsService.initializeForUser(event.userId());
    }
}
