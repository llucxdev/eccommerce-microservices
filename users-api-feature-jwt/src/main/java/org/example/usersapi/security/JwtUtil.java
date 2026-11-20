package org.example.usersapi.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtUtil {

    @Value("${jwt.subject}")
    private String subject;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Map<String, String> claims) {
        return Jwts.builder().id(UUID.randomUUID().toString()).claims(claims).subject(subject).issuer(issuer).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 6000000)).signWith(getSecretKey(), Jwts.SIG.HS256).compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
    }
}
