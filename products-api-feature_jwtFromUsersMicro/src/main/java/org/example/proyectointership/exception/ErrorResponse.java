package org.example.proyectointership.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Schema(description = "HTTP status code of the error", example = "400")
    private int status;

    @Schema(description = "Message describing the error", example = "Invalid parameters")
    private String message;

    @Schema(description = "Date and time when the error occurred", example = "2023-04-05T10:15:30")
    private LocalDateTime timestamp;

    @Schema(description = "Validation details, if applicable. This field is optional.",
            nullable = true)
    private List<ValidationError> validationErrors;

    public ErrorResponse() {
    }

    public ErrorResponse(String message, int status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }
}