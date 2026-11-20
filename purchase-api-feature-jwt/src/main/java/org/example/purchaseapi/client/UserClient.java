package org.example.purchaseapi.client;

import org.example.purchaseapi.exception.UserClientErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class UserClient {

    @Value("${auth.email}")
    private String email;

    @Value("${auth.password}")
    private String password;

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

    public Map<String, String> getClaims(String token) {
        return validateToken(token)
                .onErrorResume(errorHandler::handleMapError)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Unauthorized: Token validation failed"));
    }

    private Mono<Map<String, String>> validateToken(String token) {
        return webClient.post()
                .uri("/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        errorHandler.handle4xxErrors(response).flatMap(Mono::error))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        errorHandler.handle5xxErrors(response).flatMap(Mono::error))
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, String>>() {
                });
    }

    public String getTechnicalToken() {
        return getToken()
                .onErrorResume(errorHandler::handleError)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Unauthorized: Token validation failed"));
    }

    private Mono<String> getToken() {
        String jsonBody = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        return webClient.post()
                .uri("/auth/login")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(jsonBody), String.class)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        errorHandler.handle4xxErrors(response).flatMap(Mono::error))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        errorHandler.handle5xxErrors(response).flatMap(Mono::error))
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<String>() {
                });
    }
}