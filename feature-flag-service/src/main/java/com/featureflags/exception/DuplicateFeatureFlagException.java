package com.featureflags.exception;

public class DuplicateFeatureFlagException extends RuntimeException {

    public DuplicateFeatureFlagException(String message) {
        super(message);
    }

    public DuplicateFeatureFlagException(String message, Throwable cause) {
        super(message, cause);
    }
}
