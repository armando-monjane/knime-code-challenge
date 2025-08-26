package com.featureflags.controller;

import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.service.FeatureFlagService;
import com.featureflags.exception.FeatureFlagNotFoundException;
import com.featureflags.exception.DuplicateFeatureFlagException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
@CrossOrigin(origins = "*")
public class FeatureFlagController {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagController.class);

    private final FeatureFlagService featureFlagService;

    @Autowired
    public FeatureFlagController(FeatureFlagService featureFlagService) {
        this.featureFlagService = featureFlagService;
    }

    @GetMapping
    public ResponseEntity<List<FeatureFlagDto>> getAllFlags() {
        List<FeatureFlagDto> flags = featureFlagService.getAllFlags();
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeatureFlagDto> getFlagById(@PathVariable Long id) {
        try {
            FeatureFlagDto flag = featureFlagService.getFlagById(id);
            return ResponseEntity.ok(flag);
        } catch (FeatureFlagNotFoundException e) {
            logger.warn("Feature flag not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<FeatureFlagDto> getFlagByName(@PathVariable String name) {
        try {
            FeatureFlagDto flag = featureFlagService.getFlagByName(name);
            return ResponseEntity.ok(flag);
        } catch (FeatureFlagNotFoundException e) {
            logger.warn("Feature flag not found with name: {}", name);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{name}/enabled")
    public ResponseEntity<Boolean> isFlagEnabled(@PathVariable String name) {
        boolean enabled = featureFlagService.isFlagEnabled(name);
        return ResponseEntity.ok(enabled);
    }

    @PostMapping
    public ResponseEntity<FeatureFlagDto> createFlag(@Valid @RequestBody FeatureFlagDto flagDto) {
        try {
            FeatureFlagDto createdFlag = featureFlagService.createFlag(flagDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFlag);
        } catch (DuplicateFeatureFlagException e) {
            logger.warn("Duplicate feature flag creation attempted: {}", flagDto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeatureFlagDto> updateFlag(@PathVariable Long id, 
                                                    @Valid @RequestBody FeatureFlagDto flagDto) {
        try {
            FeatureFlagDto updatedFlag = featureFlagService.updateFlag(id, flagDto);
            return ResponseEntity.ok(updatedFlag);
        } catch (FeatureFlagNotFoundException e) {
            logger.warn("Feature flag not found for update with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (DuplicateFeatureFlagException e) {
            logger.warn("Duplicate feature flag name in update: {}", flagDto.getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<FeatureFlagDto> toggleFlag(@PathVariable Long id) {
        try {
            FeatureFlagDto toggledFlag = featureFlagService.toggleFlag(id);
            return ResponseEntity.ok(toggledFlag);
        } catch (FeatureFlagNotFoundException e) {
            logger.warn("Feature flag not found for toggle with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlag(@PathVariable Long id) {
        try {
            featureFlagService.deleteFlag(id);
            return ResponseEntity.noContent().build();
        } catch (FeatureFlagNotFoundException e) {
            logger.warn("Feature flag not found for deletion with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<FeatureFlagDto>> getEnabledFlags() {
        List<FeatureFlagDto> flags = featureFlagService.getEnabledFlags();
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/disabled")
    public ResponseEntity<List<FeatureFlagDto>> getDisabledFlags() {
        List<FeatureFlagDto> flags = featureFlagService.getDisabledFlags();
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FeatureFlagDto>> searchFlags(@RequestParam String name) {
        List<FeatureFlagDto> flags = featureFlagService.searchFlagsByName(name);
        return ResponseEntity.ok(flags);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Feature Flag Service is healthy");
    }
}
