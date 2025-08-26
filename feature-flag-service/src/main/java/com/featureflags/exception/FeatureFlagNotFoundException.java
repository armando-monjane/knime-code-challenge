package com.featureflags.exception;

public class FeatureFlagNotFoundException extends RuntimeException {

    public FeatureFlagNotFoundException(String message) {
        super(message);
    }

    public FeatureFlagNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
