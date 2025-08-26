package com.featureflags.service;

import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.model.FeatureFlag;
import com.featureflags.repository.FeatureFlagRepository;
import com.featureflags.exception.FeatureFlagNotFoundException;
import com.featureflags.exception.DuplicateFeatureFlagException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServiceTest {

    @Mock
    private FeatureFlagRepository featureFlagRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private FeatureFlagService featureFlagService;

    private FeatureFlag testFlag;
    private FeatureFlagDto testFlagDto;

    @BeforeEach
    void setUp() {
        testFlag = new FeatureFlag();
        testFlag.setId(1L);
        testFlag.setName("test_flag");
        testFlag.setEnabled(true);
        testFlag.setDescription("Test flag description");
        testFlag.setCreatedAt(LocalDateTime.now());
        testFlag.setUpdatedAt(LocalDateTime.now());

        testFlagDto = new FeatureFlagDto();
        testFlagDto.setName("test_flag");
        testFlagDto.setEnabled(true);
        testFlagDto.setDescription("Test flag description");
    }

    @Test
    void getAllFlags_ShouldReturnAllFlags() {
        // Given
        List<FeatureFlag> flags = Arrays.asList(testFlag);
        when(featureFlagRepository.findAll()).thenReturn(flags);

        // When
        List<FeatureFlagDto> result = featureFlagService.getAllFlags();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlag.getName(), result.get(0).getName());
        verify(featureFlagRepository).findAll();
    }

    @Test
    void getFlagById_WhenFlagExists_ShouldReturnFlag() {
        // Given
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        // When
        FeatureFlagDto result = featureFlagService.getFlagById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testFlag.getName(), result.getName());
        assertEquals(testFlag.isEnabled(), result.isEnabled());
        verify(featureFlagRepository).findById(1L);
    }

    @Test
    void getFlagById_WhenFlagDoesNotExist_ShouldThrowException() {
        // Given
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(FeatureFlagNotFoundException.class, () -> {
            featureFlagService.getFlagById(1L);
        });
        verify(featureFlagRepository).findById(1L);
    }

    @Test
    void createFlag_WhenFlagDoesNotExist_ShouldCreateFlag() {
        // Given
        when(featureFlagRepository.existsByName(testFlagDto.getName())).thenReturn(false);
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        // When
        FeatureFlagDto result = featureFlagService.createFlag(testFlagDto);

        // Then
        assertNotNull(result);
        assertEquals(testFlag.getName(), result.getName());
        verify(featureFlagRepository).existsByName(testFlagDto.getName());
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void createFlag_WhenFlagExists_ShouldThrowException() {
        // Given
        when(featureFlagRepository.existsByName(testFlagDto.getName())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateFeatureFlagException.class, () -> {
            featureFlagService.createFlag(testFlagDto);
        });
        verify(featureFlagRepository).existsByName(testFlagDto.getName());
        verify(featureFlagRepository, never()).save(any());
    }

    @Test
    void updateFlag_WhenFlagExists_ShouldUpdateFlag() {
        // Given
        FeatureFlagDto updateDto = new FeatureFlagDto();
        updateDto.setName("updated_flag");
        updateDto.setEnabled(false);
        updateDto.setDescription("Updated description");

        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(featureFlagRepository.existsByName(updateDto.getName())).thenReturn(false);
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        // When
        FeatureFlagDto result = featureFlagService.updateFlag(1L, updateDto);

        // Then
        assertNotNull(result);
        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void toggleFlag_WhenFlagExists_ShouldToggleFlag() {
        // Given
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        // When
        FeatureFlagDto result = featureFlagService.toggleFlag(1L);

        // Then
        assertNotNull(result);
        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void deleteFlag_WhenFlagExists_ShouldDeleteFlag() {
        // Given
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        // When
        featureFlagService.deleteFlag(1L);

        // Then
        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).delete(testFlag);
    }

    @Test
    void isFlagEnabled_WhenFlagExists_ShouldReturnStatus() {
        // Given
        when(featureFlagRepository.findByName("test_flag")).thenReturn(Optional.of(testFlag));

        // When
        boolean result = featureFlagService.isFlagEnabled("test_flag");

        // Then
        assertTrue(result);
        verify(featureFlagRepository).findByName("test_flag");
    }

    @Test
    void isFlagEnabled_WhenFlagDoesNotExist_ShouldReturnFalse() {
        // Given
        when(featureFlagRepository.findByName("nonexistent_flag")).thenReturn(Optional.empty());

        // When
        boolean result = featureFlagService.isFlagEnabled("nonexistent_flag");

        // Then
        assertFalse(result);
        verify(featureFlagRepository).findByName("nonexistent_flag");
    }

    @Test
    void getEnabledFlags_ShouldReturnOnlyEnabledFlags() {
        // Given
        List<FeatureFlag> enabledFlags = Arrays.asList(testFlag);
        when(featureFlagRepository.findByEnabled(true)).thenReturn(enabledFlags);

        // When
        List<FeatureFlagDto> result = featureFlagService.getEnabledFlags();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isEnabled());
        verify(featureFlagRepository).findByEnabled(true);
    }

    @Test
    void getDisabledFlags_ShouldReturnOnlyDisabledFlags() {
        // Given
        FeatureFlag disabledFlag = new FeatureFlag();
        disabledFlag.setId(2L);
        disabledFlag.setName("disabled_flag");
        disabledFlag.setEnabled(false);
        
        List<FeatureFlag> disabledFlags = Arrays.asList(disabledFlag);
        when(featureFlagRepository.findByEnabled(false)).thenReturn(disabledFlags);

        // When
        List<FeatureFlagDto> result = featureFlagService.getDisabledFlags();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isEnabled());
        verify(featureFlagRepository).findByEnabled(false);
    }
}
