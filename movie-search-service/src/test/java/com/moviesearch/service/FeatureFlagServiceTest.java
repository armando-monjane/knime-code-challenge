package com.moviesearch.service;

import com.moviesearch.dto.FlagUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    private FeatureFlagService featureFlagService;

    @BeforeEach
    void setUp() {
        featureFlagService = new FeatureFlagService();
    }

    @Test
    void isFlagEnabled_WhenFlagNotSet_ShouldReturnFalse() {
        assertFalse(featureFlagService.isFlagEnabled("nonexistent_flag"));
    }

    @Test
    void handleFlagUpdate_WhenFlagCreated_ShouldSetFlag() {
        FlagUpdateEvent event = new FlagUpdateEvent("test_flag", true, "CREATED");
        event.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(event);

        assertTrue(featureFlagService.isFlagEnabled("test_flag"));
    }

    @Test
    void handleFlagUpdate_WhenFlagUpdated_ShouldUpdateFlag() {
        FlagUpdateEvent createEvent = new FlagUpdateEvent("test_flag", true, "CREATED");
        FlagUpdateEvent updateEvent = new FlagUpdateEvent("test_flag", false, "UPDATED");
        createEvent.setTimestamp(LocalDateTime.now());
        updateEvent.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(createEvent);
        featureFlagService.handleFlagUpdate(updateEvent);

        assertFalse(featureFlagService.isFlagEnabled("test_flag"));
    }

    @Test
    void handleFlagUpdate_WhenFlagToggled_ShouldToggleFlag() {
        FlagUpdateEvent createEvent = new FlagUpdateEvent("test_flag", true, "CREATED");
        FlagUpdateEvent toggleEvent = new FlagUpdateEvent("test_flag", false, "TOGGLED");
        createEvent.setTimestamp(LocalDateTime.now());
        toggleEvent.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(createEvent);
        featureFlagService.handleFlagUpdate(toggleEvent);

        assertFalse(featureFlagService.isFlagEnabled("test_flag"));
    }

    @Test
    void handleFlagUpdate_WhenFlagDeleted_ShouldRemoveFlag() {
        FlagUpdateEvent createEvent = new FlagUpdateEvent("test_flag", true, "CREATED");
        FlagUpdateEvent deleteEvent = new FlagUpdateEvent("test_flag", true, "DELETED");
        createEvent.setTimestamp(LocalDateTime.now());
        deleteEvent.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(createEvent);
        featureFlagService.handleFlagUpdate(deleteEvent);

        assertFalse(featureFlagService.isFlagEnabled("test_flag"));
    }

    @Test
    void handleFlagUpdate_WithInvalidEvent_ShouldNotThrowException() {
        FlagUpdateEvent nullEvent = null;
        FlagUpdateEvent invalidEvent = new FlagUpdateEvent(null, true, "INVALID");

        assertDoesNotThrow(() -> {
            featureFlagService.handleFlagUpdate(nullEvent);
            featureFlagService.handleFlagUpdate(invalidEvent);
        });
    }

    @Test
    void isMaintenanceMode_WhenMaintenanceFlagEnabled_ShouldReturnTrue() {
        FlagUpdateEvent event = new FlagUpdateEvent("maintenance_mode", true, "CREATED");
        event.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(event);

        assertTrue(featureFlagService.isMaintenanceMode());
    }

    @Test
    void isMaintenanceMode_WhenMaintenanceFlagDisabled_ShouldReturnFalse() {
        FlagUpdateEvent event = new FlagUpdateEvent("maintenance_mode", false, "CREATED");
        event.setTimestamp(LocalDateTime.now());

        featureFlagService.handleFlagUpdate(event);

        assertFalse(featureFlagService.isMaintenanceMode());
    }
}
