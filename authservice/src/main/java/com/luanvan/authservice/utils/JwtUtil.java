package com.luanvan.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String subject, Map<String, Object> claims, boolean isRefresh) {
        long expiryTime = isRefresh ? refreshExpiration : expiration;
        claims.put("iss", issuer);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(new SecretKeySpec(key.getEncoded(), "HmacSHA512"))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractClaims(token);
            String issuer = claims.getIssuer(); // Lấy giá trị "iss"

            // Kiểm tra issuer có hợp lệ không
            if (!this.issuer.equals(issuer)) {
                return false;
            }
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
