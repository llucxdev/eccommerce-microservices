package org.example.usersapi.exception;

public class UnauthorizecAccessException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Unauthorized access";
    public UnauthorizecAccessException() {
        super(DEFAULT_MESSAGE);
    }
}
