package com.featureflags.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.featureflags.dto.FeatureFlagDto;
import com.featureflags.model.FeatureFlag;
import com.featureflags.repository.FeatureFlagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FeatureFlagIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeatureFlagRepository featureFlagRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    private FeatureFlag testFlag;

    @BeforeEach
    void setUp() {
        testFlag = new FeatureFlag();
        testFlag.setName("test_flag");
        testFlag.setEnabled(true);
        testFlag.setDescription("Test flag description");
        testFlag = featureFlagRepository.save(testFlag);
    }

    @AfterEach
    void tearDown() {
        featureFlagRepository.deleteAll();
    }

    @Test
    void getAllFlags_ShouldReturnAllFlags() throws Exception {
        mockMvc.perform(get("/api/flags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("test_flag")))
                .andExpect(jsonPath("$[0].enabled", is(true)));
    }

    @Test
    void createFlag_WithValidData_ShouldCreateFlag() throws Exception {
        FeatureFlagDto newFlag = new FeatureFlagDto();
        newFlag.setName("new_flag");
        newFlag.setEnabled(false);
        newFlag.setDescription("New flag description");

        mockMvc.perform(post("/api/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFlag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("new_flag")))
                .andExpect(jsonPath("$.enabled", is(false)));

        assertTrue(featureFlagRepository.existsByName("new_flag"));
    }

    @Test
    void createFlag_WithDuplicateName_ShouldReturnError() throws Exception {
        FeatureFlagDto duplicateFlag = new FeatureFlagDto();
        duplicateFlag.setName("test_flag");
        duplicateFlag.setEnabled(false);

        mockMvc.perform(post("/api/flags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateFlag)))
                .andExpect(status().isConflict());
    }

    @Test
    void updateFlag_WithValidData_ShouldUpdateFlag() throws Exception {
        FeatureFlagDto updateDto = new FeatureFlagDto();
        updateDto.setName("updated_flag");
        updateDto.setEnabled(false);
        updateDto.setDescription("Updated description");

        mockMvc.perform(put("/api/flags/" + testFlag.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("updated_flag")))
                .andExpect(jsonPath("$.enabled", is(false)));
    }

    @Test
    void toggleFlag_ShouldToggleFlag() throws Exception {
        mockMvc.perform(patch("/api/flags/" + testFlag.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(false)));

        mockMvc.perform(patch("/api/flags/" + testFlag.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled", is(true)));
    }

    @Test
    void deleteFlag_ShouldDeleteFlag() throws Exception {
        mockMvc.perform(delete("/api/flags/" + testFlag.getId()))
                .andExpect(status().isNoContent());

        assertFalse(featureFlagRepository.existsById(testFlag.getId()));
    }

    @Test
    void getEnabledFlags_ShouldReturnOnlyEnabledFlags() throws Exception {
        // Create a disabled flag
        FeatureFlag disabledFlag = new FeatureFlag();
        disabledFlag.setName("disabled_flag");
        disabledFlag.setEnabled(false);
        featureFlagRepository.save(disabledFlag);

        mockMvc.perform(get("/api/flags/enabled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("test_flag")))
                .andExpect(jsonPath("$[0].enabled", is(true)));
    }

    @Test
    void searchFlags_ShouldReturnMatchingFlags() throws Exception {
        mockMvc.perform(get("/api/flags/search?name=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("test_flag")));

        mockMvc.perform(get("/api/flags/search?name=nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
