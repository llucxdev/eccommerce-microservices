package org.example.proyectointership.client;

import org.example.proyectointership.exception.UserClientErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserClient {

    private final WebClient webClient;
    private final UserClientErrorHandler errorHandler;

    @Autowired
    public UserClient(@Value("${users.api.url}") String usersApiUrl, UserClientErrorHandler errorHandler) {
        this.webClient = WebClient.builder()
                .baseUrl(usersApiUrl)
                .build();
        this.errorHandler = errorHandler;
    }

    public UserClient(WebClient webClient, UserClientErrorHandler errorHandler) {
        this.webClient = webClient;
        this.errorHandler = errorHandler;
    }

    public Map<String, Object> getClaims(String token) {
        return validateToken(token)
                .onErrorResume(errorHandler::handleError)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Unauthorized: Token validation failed"));
    }

    private Mono<Map<String, Object>> validateToken(String token) {
        return webClient.post()
                .uri("/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        errorHandler.handle4xxErrors(response).flatMap(Mono::error))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        errorHandler.handle5xxErrors(response).flatMap(Mono::error))
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {
                });
    }
}