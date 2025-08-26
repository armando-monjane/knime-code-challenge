package com.moviesearch.exception;

public class MaintenanceModeException extends RuntimeException {

    public MaintenanceModeException(String message) {
        super(message);
    }

    public MaintenanceModeException(String message, Throwable cause) {
        super(message, cause);
    }
}
