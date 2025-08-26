package com.moviesearch.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;

public class FlagUpdateEvent {

    private String flagName;
    private boolean enabled;
    private String eventType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime timestamp;

    // Constructors
    public FlagUpdateEvent() {}

    public FlagUpdateEvent(String flagName, boolean enabled, String eventType) {
        this.flagName = flagName;
        this.enabled = enabled;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FlagUpdateEvent{" +
                "flagName='" + flagName + '\'' +
                ", enabled=" + enabled +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}