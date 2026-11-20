package org.example.purchaseapi.exception;

import org.example.purchaseapi.dto.ProductResponse;
import org.example.purchaseapi.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Component
public class ProductClientErrorHandler {
    public Mono<? extends Throwable> handle4xxErrors(ClientResponse response) {
        if (response.statusCode() == HttpStatus.NOT_FOUND) {
            return response.bodyToMono(ErrorResponse.class)
                    .flatMap(errorResponse -> Mono.error(new ProductNotFoundException(errorResponse.getMessage())));
        }
        return Mono.error(new CustomException("Unexpected client error"));
    }

    public Mono<? extends Throwable> handle5xxErrors(ClientResponse response) {
        return response.bodyToMono(ErrorResponse.class)
                .flatMap(errorResponse -> Mono.error(new CustomException(errorResponse.getMessage())));
    }

    public Mono<ProductResponse> handleError(Throwable throwable) {
        return Mono.error(throwable);
    }
}
