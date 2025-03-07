package com.luanvan.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.crypto.AlgorithmMethod;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
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
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(decodedKey);
    }

    public String generateToken(String subject, Map<String, Object> claims, boolean isRefresh) {
        long expiryTime = isRefresh ? refreshExpiration : expiration;
        claims.put("iss", issuer);

        log.info("key: {}", Base64.getEncoder().encodeToString(key.getEncoded()));
        log.info("issuer: {}", issuer);
        log.info("subject: {}", subject);
        log.info("claims: {}", claims);
        try {
           String result = Jwts.builder()
                   .setClaims(claims)
                   .setSubject(subject)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                   .signWith(key)
                   .compact();
           log.info(result);
           return result;
        }catch (Exception e) {
           log.info(e.getMessage());
        }
        return null;
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
            Instant now = Instant.now();
            Instant exp = claims.getExpiration().toInstant();
            if (now.isAfter(exp)) {
                return false;
            }
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
