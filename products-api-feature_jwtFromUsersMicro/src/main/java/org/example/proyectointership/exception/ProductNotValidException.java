package org.example.proyectointership.exception;

import java.util.List;

public class ProductNotValidException extends Exception {
    private List<ValidationError> errors;

    public ProductNotValidException() {}

    public ProductNotValidException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}