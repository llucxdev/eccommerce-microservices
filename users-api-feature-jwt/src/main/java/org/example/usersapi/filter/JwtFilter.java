package org.example.usersapi.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.example.usersapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Profile("!test")
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private final List<String> excludedPaths = List.of(
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs*/**",
            "/h2-console/**"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private boolean isExcluded(String path) {
        return excludedPaths.stream().anyMatch(excluded -> pathMatcher.match(excluded, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isExcluded(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.parseClaims(token);
            if (claims.get("email") == null) {
                sendUnauthorized(response, "Invalid token: missing email claim");
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendUnauthorized(response, "Invalid or expired token");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
    }
}
