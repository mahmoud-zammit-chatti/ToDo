package com.mahmoud.todo.exceptions;



import java.time.Instant;
import java.util.List;


public class ApiError {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<ValidationErrorDetail> details;

    public ApiError(int status, String error, String message, String path, List<ValidationErrorDetail> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<ValidationErrorDetail> getDetails() {
        return details;
    }
}