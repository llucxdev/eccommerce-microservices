package org.example.proyectointership.client;

import org.example.proyectointership.exception.UserIsNotAdminException;
import org.example.proyectointership.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AdminValidatorTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private AdminValidator adminValidator;

    private String token;

    @BeforeEach
    void setUp() {
        token = "token";
    }

    @Test
    void validateAdmin_userIsAdmin_returnsTrue() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", true);

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(claims);

        // Then
        assertDoesNotThrow(() -> adminValidator.validateAdmin(token));
    }

    @Test
    void validateAdmin_userIsNotAdmin_throwsUserIsNotAdminException() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("isAdmin", false);
        claims.put("userId", 1);

        // When
        Mockito.when(userClient.getClaims(token)).thenReturn(claims);

        // Then
        assertThrows(UserIsNotAdminException.class, () -> adminValidator.validateAdmin(token));
    }

    @Test
    void validateAdmin_userNotInDatabase_throwsUserNotFoundException() {
        // Given

        // When
        Mockito.when(userClient.getClaims(token)).thenThrow(UserNotFoundException.class);

        // Then
        assertThrows(UserNotFoundException.class, () -> adminValidator.validateAdmin(token));
    }
}