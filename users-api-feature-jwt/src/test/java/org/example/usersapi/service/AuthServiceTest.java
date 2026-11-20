package org.example.usersapi.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.example.usersapi.dto.LoginData;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.example.usersapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthService authService;

    private LoginData loginData;
    private User user;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    void setUp() {
        loginData = new LoginData("test@example.com", "testing");
        user = new User(1L, "test", "test@example.com", "testing");
        user.setPassword(encoder.encode(loginData.getPassword()));
    }

    @Test
    void checkLogin_validLogin_returnsToken() {
        // Given
        String expected = "fakeToken";

        // When

        Mockito.when(userRepository.findByEmail(loginData.getEmail())).thenReturn(Optional.ofNullable(user));
        Mockito.when(jwtUtil.generateToken(anyMap())).thenReturn(expected);
        String actual = authService.checkLogin(loginData);

        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void checkLogin_invalidUser_throwsAuthorizationDeniedException() {
        // Given

        // When
        Mockito.when(userRepository.findByEmail(loginData.getEmail())).thenThrow(new AuthorizationDeniedException("Invalid email or password"));

        // Then
        assertThrows(AuthorizationDeniedException.class, () -> authService.checkLogin(loginData));
    }
}