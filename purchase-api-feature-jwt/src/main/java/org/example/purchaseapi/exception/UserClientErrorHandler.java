package org.example.purchaseapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserClientErrorHandler {
    public Mono<? extends Throwable> handle4xxErrors(ClientResponse response) {
        if (response.statusCode() == HttpStatus.NOT_FOUND) {
            return response.bodyToMono(ErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new UserNotFoundException(errorResponse.getMessage())));
        }
        return Mono.error(new CustomException("Unexpected client error"));
    }

    public Mono<? extends Throwable> handle5xxErrors(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new CustomException(errorResponse.getMessage())));
    }

    public Mono<Map<String, String>> handleMapError(Throwable throwable) {
        return Mono.error(throwable);
    }

    public Mono<String> handleError(Throwable throwable) {
        return Mono.error(throwable);
    }
}
