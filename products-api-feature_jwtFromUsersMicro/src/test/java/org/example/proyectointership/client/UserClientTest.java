//package org.example.proyectointership.client;
//
//import org.example.proyectointership.exception.UserClientErrorHandler;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//
//@ExtendWith(MockitoExtension.class)
//class UserClientTest {
//
//    @Mock
//    private WebClient webClient;
//
//    @Mock
//    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
//
//    @Mock
//    private WebClient.RequestHeadersSpec requestHeadersSpec;
//
//    @Mock
//    private WebClient.ResponseSpec responseSpec;
//
//    @Mock
//    private UserClientErrorHandler errorHandler;
//
//    @InjectMocks
//    private UserClient userClient;
//
//    @Test
//    void isUserAdmin_adminUser_returnsTrue() {
//        // Given
//        Long userId = 1L;
//
//        // When
//        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        Mockito.when(requestHeadersUriSpec.uri("/api/users/{id}/is-admin", userId)).thenReturn(requestHeadersSpec);
//        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        Mockito.when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//        Mockito.when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(true));
//        boolean result = userClient.isUserAdmin(userId);
//
//        // Then
//        assertTrue(result);
//    }
//
//    @Test
//    void isUserAdmin_notAdminUser_returnsFalse() {
//        // Given
//        Long userId = 1L;
//
//        // When
//        Mockito.when(webClient.get()).thenReturn(requestHeadersUriSpec);
//        Mockito.when(requestHeadersUriSpec.uri("/api/users/{id}/is-admin", userId)).thenReturn(requestHeadersSpec);
//        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//        Mockito.when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
//        Mockito.when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.just(false));
//        boolean result = userClient.isUserAdmin(userId);
//
//        // Then
//        assertFalse(result);
//    }
//}