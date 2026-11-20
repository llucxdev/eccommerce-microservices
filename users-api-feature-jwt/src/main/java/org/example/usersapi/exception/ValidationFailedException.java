package org.example.usersapi.exception;

import java.util.List;

public class ValidationFailedException extends Exception {
    private List<ValidationError> errors;

    public ValidationFailedException() {}

    public ValidationFailedException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}