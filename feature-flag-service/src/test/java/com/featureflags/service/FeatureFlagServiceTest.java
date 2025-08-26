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
        List<FeatureFlag> flags = Arrays.asList(testFlag);
        when(featureFlagRepository.findAll()).thenReturn(flags);

        List<FeatureFlagDto> result = featureFlagService.getAllFlags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFlag.getName(), result.get(0).getName());
        verify(featureFlagRepository).findAll();
    }

    @Test
    void getFlagById_WhenFlagExists_ShouldReturnFlag() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        FeatureFlagDto result = featureFlagService.getFlagById(1L);

        assertNotNull(result);
        assertEquals(testFlag.getName(), result.getName());
        assertEquals(testFlag.isEnabled(), result.isEnabled());
        verify(featureFlagRepository).findById(1L);
    }

    @Test
    void getFlagById_WhenFlagDoesNotExist_ShouldThrowException() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FeatureFlagNotFoundException.class, () -> {
            featureFlagService.getFlagById(1L);
        });
        verify(featureFlagRepository).findById(1L);
    }

    @Test
    void createFlag_WhenFlagDoesNotExist_ShouldCreateFlag() {
        when(featureFlagRepository.existsByName(testFlagDto.getName())).thenReturn(false);
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        FeatureFlagDto result = featureFlagService.createFlag(testFlagDto);

        assertNotNull(result);
        assertEquals(testFlag.getName(), result.getName());
        verify(featureFlagRepository).existsByName(testFlagDto.getName());
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void createFlag_WhenFlagExists_ShouldThrowException() {
        when(featureFlagRepository.existsByName(testFlagDto.getName())).thenReturn(true);

        assertThrows(DuplicateFeatureFlagException.class, () -> {
            featureFlagService.createFlag(testFlagDto);
        });
        verify(featureFlagRepository).existsByName(testFlagDto.getName());
        verify(featureFlagRepository, never()).save(any());
    }

    @Test
    void updateFlag_WhenFlagExists_ShouldUpdateFlag() {
        FeatureFlagDto updateDto = new FeatureFlagDto();
        updateDto.setName("updated_flag");
        updateDto.setEnabled(false);
        updateDto.setDescription("Updated description");

        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(featureFlagRepository.existsByName(updateDto.getName())).thenReturn(false);
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        FeatureFlagDto result = featureFlagService.updateFlag(1L, updateDto);

        assertNotNull(result);
        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void toggleFlag_WhenFlagExists_ShouldToggleFlag() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));
        when(featureFlagRepository.save(any(FeatureFlag.class))).thenReturn(testFlag);

        FeatureFlagDto result = featureFlagService.toggleFlag(1L);

        assertNotNull(result);
        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).save(any(FeatureFlag.class));
    }

    @Test
    void deleteFlag_WhenFlagExists_ShouldDeleteFlag() {
        when(featureFlagRepository.findById(1L)).thenReturn(Optional.of(testFlag));

        featureFlagService.deleteFlag(1L);

        verify(featureFlagRepository).findById(1L);
        verify(featureFlagRepository).delete(testFlag);
    }

    @Test
    void isFlagEnabled_WhenFlagExists_ShouldReturnStatus() {
        when(featureFlagRepository.findByName("test_flag")).thenReturn(Optional.of(testFlag));

        boolean result = featureFlagService.isFlagEnabled("test_flag");

        assertTrue(result);
        verify(featureFlagRepository).findByName("test_flag");
    }

    @Test
    void isFlagEnabled_WhenFlagDoesNotExist_ShouldReturnFalse() {
        when(featureFlagRepository.findByName("nonexistent_flag")).thenReturn(Optional.empty());

        boolean result = featureFlagService.isFlagEnabled("nonexistent_flag");

        assertFalse(result);
        verify(featureFlagRepository).findByName("nonexistent_flag");
    }

    @Test
    void getEnabledFlags_ShouldReturnOnlyEnabledFlags() {
        List<FeatureFlag> enabledFlags = Arrays.asList(testFlag);
        when(featureFlagRepository.findByEnabled(true)).thenReturn(enabledFlags);

        List<FeatureFlagDto> result = featureFlagService.getEnabledFlags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isEnabled());
        verify(featureFlagRepository).findByEnabled(true);
    }

    @Test
    void getDisabledFlags_ShouldReturnOnlyDisabledFlags() {
        FeatureFlag disabledFlag = new FeatureFlag();
        disabledFlag.setId(2L);
        disabledFlag.setName("disabled_flag");
        disabledFlag.setEnabled(false);
        
        List<FeatureFlag> disabledFlags = Arrays.asList(disabledFlag);
        when(featureFlagRepository.findByEnabled(false)).thenReturn(disabledFlags);

        List<FeatureFlagDto> result = featureFlagService.getDisabledFlags();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isEnabled());
        verify(featureFlagRepository).findByEnabled(false);
    }
}
