package com.featureflags.service;

import com.featureflags.dto.FlagUpdateEvent;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.model.FeatureFlag;
import com.featureflags.repository.FeatureFlagRepository;
import com.featureflags.exception.FeatureFlagNotFoundException;
import com.featureflags.exception.DuplicateFeatureFlagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeatureFlagService {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagService.class);
    private static final String EXCHANGE_NAME = "feature-flags";
    private static final String ROUTING_KEY = "flag.update";

    private final FeatureFlagRepository featureFlagRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public FeatureFlagService(FeatureFlagRepository featureFlagRepository, RabbitTemplate rabbitTemplate) {
        this.featureFlagRepository = featureFlagRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public List<FeatureFlagDto> getAllFlags() {
        return featureFlagRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FeatureFlagDto getFlagById(Long id) {
        FeatureFlag flag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature flag not found with id: " + id));
        return convertToDto(flag);
    }

    public FeatureFlagDto getFlagByName(String name) {
        FeatureFlag flag = featureFlagRepository.findByName(name)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature flag not found with name: " + name));
        return convertToDto(flag);
    }

    public boolean isFlagEnabled(String name) {
        Optional<FeatureFlag> flag = featureFlagRepository.findByName(name);
        return flag.map(FeatureFlag::isEnabled).orElse(false);
    }

    public FeatureFlagDto createFlag(FeatureFlagDto flagDto) {
        if (featureFlagRepository.existsByName(flagDto.getName())) {
            throw new DuplicateFeatureFlagException("Feature flag with name '" + flagDto.getName() + "' already exists");
        }

        FeatureFlag flag = convertToEntity(flagDto);
        FeatureFlag savedFlag = featureFlagRepository.save(flag);
        
        // Publish event for flag creation
        publishFlagEvent(savedFlag.getName(), savedFlag.isEnabled(), "CREATED");
        logger.info("Feature flag created successfully: {}", savedFlag.getName());
        
        return convertToDto(savedFlag);
    }

    public FeatureFlagDto updateFlag(Long id, FeatureFlagDto flagDto) {
        FeatureFlag existingFlag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature flag not found with id: " + id));

        // Check for name conflicts if name is being changed
        if (!existingFlag.getName().equals(flagDto.getName()) && 
            featureFlagRepository.existsByName(flagDto.getName())) {
            throw new DuplicateFeatureFlagException("Feature flag with name '" + flagDto.getName() + "' already exists");
        }

        boolean wasEnabled = existingFlag.isEnabled();
        
        existingFlag.setName(flagDto.getName());
        existingFlag.setEnabled(flagDto.isEnabled());
        existingFlag.setDescription(flagDto.getDescription());

        FeatureFlag updatedFlag = featureFlagRepository.save(existingFlag);
        
        // Publish event if enabled status changed
        if (wasEnabled != updatedFlag.isEnabled()) {
            publishFlagEvent(updatedFlag.getName(), updatedFlag.isEnabled(), "UPDATED");
        }
        
        return convertToDto(updatedFlag);
    }

    public FeatureFlagDto toggleFlag(Long id) {
        FeatureFlag flag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature flag not found with id: " + id));

        flag.setEnabled(!flag.isEnabled());
        FeatureFlag updatedFlag = featureFlagRepository.save(flag);
        
        // Publish event for flag toggle
        publishFlagEvent(updatedFlag.getName(), updatedFlag.isEnabled(), "TOGGLED");
        
        return convertToDto(updatedFlag);
    }

    public void deleteFlag(Long id) {
        FeatureFlag flag = featureFlagRepository.findById(id)
                .orElseThrow(() -> new FeatureFlagNotFoundException("Feature flag not found with id: " + id));

        String flagName = flag.getName();
        boolean wasEnabled = flag.isEnabled();
        
        featureFlagRepository.delete(flag);
        
        // Publish event for flag deletion
        publishFlagEvent(flagName, wasEnabled, "DELETED");
        logger.info("Feature flag deleted successfully: {}", flagName);
    }

    public List<FeatureFlagDto> getEnabledFlags() {
        return featureFlagRepository.findByEnabled(true)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FeatureFlagDto> getDisabledFlags() {
        return featureFlagRepository.findByEnabled(false)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<FeatureFlagDto> searchFlagsByName(String name) {
        return featureFlagRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private void publishFlagEvent(String flagName, boolean enabled, String eventType) {
        try {
            FlagUpdateEvent event = new FlagUpdateEvent(flagName, enabled, eventType);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, event);
        } catch (Exception e) {
            logger.error("Failed to publish flag event for flag: {}", flagName, e);
            //TODO: Implement retry logic
        }
    }

    private FeatureFlagDto convertToDto(FeatureFlag flag) {
        FeatureFlagDto dto = new FeatureFlagDto();
        dto.setId(flag.getId());
        dto.setName(flag.getName());
        dto.setEnabled(flag.isEnabled());
        dto.setDescription(flag.getDescription());
        return dto;
    }

    private FeatureFlag convertToEntity(FeatureFlagDto dto) {
        FeatureFlag flag = new FeatureFlag();
        flag.setName(dto.getName());
        flag.setEnabled(dto.isEnabled());
        flag.setDescription(dto.getDescription());
        return flag;
    }
}
