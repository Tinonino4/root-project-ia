package com.ia.root.backend.infrastructure.events;

import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EventRetryScheduler {

    private final IncompleteEventPublications incompletePublications;

    public EventRetryScheduler(IncompleteEventPublications incompletePublications) {
        this.incompletePublications = incompletePublications;
    }

    // Run every 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void retryFailedEventPublications() {
        // Resubmit incomplete publications older than 2 minutes
        incompletePublications.resubmitIncompletePublicationsOlderThan(Duration.ofMinutes(2));
    }
}
