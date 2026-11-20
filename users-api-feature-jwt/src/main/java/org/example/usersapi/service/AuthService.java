package org.example.usersapi.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.example.usersapi.dto.LoginData;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.example.usersapi.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public String checkLogin(LoginData loginData) {
        User user = userRepository.findByEmail(loginData.getEmail()).orElseThrow(() -> new AuthorizationDeniedException("Invalid email or password"));

        if (encoder.matches(loginData.getPassword(), user.getPassword())) {
            return getToken(user);
        } else {
            throw new AuthorizationDeniedException("Invalid email or password");
        }
    }

    public Map<String, Object> parseTokenToMap(String token) {
        Claims claims = jwtUtil.parseClaims(token.substring(7));
        if (claims == null) throw new AuthorizationDeniedException("Invalid token");
        Map<String, Object> response = new HashMap<>();
        response.put("userId", claims.get("userId"));
        response.put("email", claims.get("email"));
        response.put("isAdmin", claims.get("isAdmin"));
        return response;
    }

    private Map<String, String> getClaims(User user) {
        Map<String, String> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("userId", Long.toString(user.getId()));
        claims.put("isAdmin", Boolean.toString(user.isAdmin()));
        return claims;
    }

    private String getToken(User user) {
        return jwtUtil.generateToken(getClaims(user));
    }

    public boolean isAdmin() {
        return Boolean.parseBoolean(jwtUtil.parseClaims(this.request.getHeader("Authorization").substring(7)).get("isAdmin", String.class));
    }

    public boolean isSameUser(Long userId) {
        return userId == Long.parseLong(jwtUtil.parseClaims(this.request.getHeader("Authorization").substring(7)).get("userId", String.class));
    }
}
