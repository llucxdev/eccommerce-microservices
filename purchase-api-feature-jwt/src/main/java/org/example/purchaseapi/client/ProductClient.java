package org.example.purchaseapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.purchaseapi.dto.ProductResponse;
import org.example.purchaseapi.exception.ProductClientErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Component
public class ProductClient {

    private final WebClient webClient;
    private final ProductClientErrorHandler productClientErrorHandler;

    @Autowired
    public ProductClient(@Value("${products.api.url}") String productsApiUrl, ProductClientErrorHandler productClientErrorHandler) {
        this.webClient = WebClient.builder().baseUrl(productsApiUrl).build();
        this.productClientErrorHandler = productClientErrorHandler;
    }

    public ProductResponse getProduct(Long productId, String token) {
        return getProductById(productId, token).onErrorResume(productClientErrorHandler::handleError).blockOptional().orElse(null);
    }

    public ProductResponse updateProduct(Long productId, int stock, String token) {
        return updateProductById(productId, stock, token).onErrorResume(productClientErrorHandler::handleError).blockOptional().orElse(null);
    }

    private Mono<ProductResponse> getProductById(Long productId, String token) {
        return webClient.get()
                .uri("/api/products/{id}", productId)
                .header("Authorization", token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        productClientErrorHandler.handle4xxErrors(response).flatMap(Mono::error))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        productClientErrorHandler.handle5xxErrors(response).flatMap(Mono::error))
                .bodyToMono(ProductResponse.class);
    }

    private Mono<ProductResponse> updateProductById(Long productId, int stock, String token) {
        String bearerToken = "Bearer " + token;
        return webClient.put()
                .uri("/api/products/{id}", productId)
                .header("Authorization", bearerToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(String.format("{\"stock\": %s }", stock)), String.class)
                .retrieve()
                .bodyToMono(ProductResponse.class);
    }
}
