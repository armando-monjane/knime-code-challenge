package com.featureflags.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FeatureFlagDto {

    private Long id;

    @NotBlank(message = "Flag name is required")
    @Size(min = 1, max = 100, message = "Flag name must be between 1 and 100 characters")
    private String name;

    private boolean enabled;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    // Constructors
    public FeatureFlagDto() {}

    public FeatureFlagDto(String name, boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    public FeatureFlagDto(String name, boolean enabled, String description) {
        this.name = name;
        this.enabled = enabled;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FeatureFlagDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                '}';
    }
}
