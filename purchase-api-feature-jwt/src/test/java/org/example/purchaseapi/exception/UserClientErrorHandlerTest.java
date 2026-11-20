package org.example.purchaseapi.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class UserClientErrorHandlerTest {

    private final UserClientErrorHandler errorHandler = new UserClientErrorHandler();

    @Test
    void handle4xxErrors_userNotFound_throwsUserNotFoundException() {
        // Given
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.NOT_FOUND)
                .body("{\"message\":\"User not found\"}")
                .header("Content-Type", "application/json")
                .build();

        // When
        Mono<? extends Throwable> actual = errorHandler.handle4xxErrors(clientResponse);

        // Then
        StepVerifier.create(actual)
                .expectErrorMatches(throwable -> throwable instanceof UserNotFoundException &&
                        throwable.getMessage().equals("User not found"))
                .verify();
    }

    @Test
    void handle4xxErrors_other4xxError_throwsCustomException() {
        // Given
        ClientResponse clientResponse = ClientResponse.create(HttpStatusCode.valueOf(400))
                .body("{\"message\":\"error\"}")
                .header("Content-Type", "application/json")
                .build();

        // When
        Mono<? extends Throwable> actual = errorHandler.handle4xxErrors(clientResponse);

        // Then

        StepVerifier.create(actual)
                .expectErrorMatches(throwable -> throwable instanceof CustomException &&
                        throwable.getMessage().equals("Unexpected client error"))
                .verify();
    }

    @Test
    void testHandle5xxErrors_returnsCustomException() {
        // Given
        ClientResponse clientResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"Internal server error\"}")
                .header("Content-Type", "application/json")
                .build();

        // When
        Mono<? extends Throwable> actual = errorHandler.handle5xxErrors(clientResponse);

        // Then

        StepVerifier.create(actual)
                .expectErrorMatches(throwable -> throwable instanceof CustomException &&
                        throwable.getMessage().equals("Internal server error"))
                .verify();
    }

    @Test
    void handleError_throwableError_returnTheSameThrowable() {
        // Given
        Throwable expectedThrowable = new CustomException("Custom error message");

        // When
        Mono<Map<String, String>> result = errorHandler.handleMapError(expectedThrowable);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CustomException &&
                        throwable.getMessage().equals("Custom error message"))
                .verify();
    }

}