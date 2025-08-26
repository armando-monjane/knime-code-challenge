package com.moviesearch.service;

import com.moviesearch.dto.FlagUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeatureFlagService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagService.class);
    private final Map<String, Boolean> featureFlags = new ConcurrentHashMap<>();

    public boolean isFlagEnabled(String flagName) {
        return featureFlags.getOrDefault(flagName, false);
    }

    @RabbitListener(queues = "feature-flag-updates", returnExceptions = "true")
    public void handleFlagUpdate(FlagUpdateEvent event) {
        if (event == null || event.getFlagName() == null) {
            logger.warn("Received invalid flag update event");
            return;
        }

        logger.info("Received flag update: {} = {} ({})", event.getFlagName(), event.isEnabled(), event.getEventType());
        
        try {
            switch (event.getEventType()) {
                case "DELETED":
                    featureFlags.remove(event.getFlagName());
                    logger.info("Removed feature flag: {}", event.getFlagName());
                    break;
                case "CREATED":
                case "UPDATED":
                case "TOGGLED":
                    featureFlags.put(event.getFlagName(), event.isEnabled());
                    logger.info("Updated feature flag: {} = {}", event.getFlagName(), event.isEnabled());
                    break;
                default:
                    logger.warn("Unknown event type: {}", event.getEventType());
            }
        } catch (Exception e) {
            logger.error("Error processing flag update event: {}", event, e);
        }
    }

    public boolean isMaintenanceMode() {
        return isFlagEnabled("maintenance_mode");
    }
}
