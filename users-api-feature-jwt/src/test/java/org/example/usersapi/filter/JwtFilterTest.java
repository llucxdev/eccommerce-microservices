package org.example.usersapi.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.usersapi.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;


    @Test
    void doFilterInternal_validToken_allow() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/api/users");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer abc123");

        // When
        Claims claims = mock(Claims.class);
        Mockito.when(jwtUtil.parseClaims("abc123")).thenReturn(claims);
        Mockito.when(claims.get("email")).thenReturn("test@example.com");
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        assertEquals("test@example.com", claims.get("email"));
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void doFilterInternal_excludedPath_allow() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/auth/login");
        Mockito.when(request.getMethod()).thenReturn("POST");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void doFilterInternal_noAuthorizationHeader_return401() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/api/users");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), eq("Missing or invalid Authorization header"));
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void doFilterInternal_tokenDoesNotStartWithBearer_return401() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/api/users");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Token abc123");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), eq("Missing or invalid Authorization header"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_notEmailClaim_return401() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/api/users");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer abc123");

        // When
        Claims claims = mock(Claims.class);
        Mockito.when(jwtUtil.parseClaims("abc123")).thenReturn(claims);
        Mockito.when(claims.get("email")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), eq("Invalid token: missing email claim"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_parseClaimsThrowsException_return401() throws ServletException, IOException {
        // Given
        Mockito.when(request.getRequestURI()).thenReturn("/api/users");
        Mockito.when(request.getMethod()).thenReturn("GET");
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer abc123");
        Mockito.when(jwtUtil.parseClaims("abc123")).thenThrow(new RuntimeException("Token parsing failed"));

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), eq("Invalid or expired token"));
        verify(filterChain, never()).doFilter(request, response);
    }
}
