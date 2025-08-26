package com.moviesearch.controller;

import com.moviesearch.service.FeatureFlagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flags")
@CrossOrigin(origins = "*")
class FlagController {
    private static final Logger logger = LoggerFactory.getLogger(FlagController.class);
    private final FeatureFlagService featureFlagService;

    @Autowired
    public FlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Boolean> getIsFlagEnabledByName(@PathVariable String name) {
        try {
            boolean isEnabled = featureFlagService.isFlagEnabled(name);
            logger.info("Flag '{}' is {}", name, isEnabled ? "enabled" : "disabled");
            return ResponseEntity.ok(isEnabled);
        } catch (Exception e) {
            logger.error("Error checking flag status for '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/maintenance_mode")
    public ResponseEntity<Boolean> isMaintenanceMode() {
        try {
            boolean isMaintenanceMode = featureFlagService.isMaintenanceMode();
            logger.info("Maintenance mode is {}", isMaintenanceMode ? "enabled" : "disabled");
            return ResponseEntity.ok(isMaintenanceMode);
        } catch (Exception e) {
            logger.error("Error checking maintenance mode: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
