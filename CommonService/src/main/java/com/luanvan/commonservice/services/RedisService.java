package com.luanvan.commonservice.services;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void storeToken(String token, long expiration) {
        if (expiration > 0) { // Chỉ lưu nếu TTL hợp lệ
            String key = "blacklist:" + token; // Thêm tiền tố để dễ quản lý
            redisTemplate.opsForValue().set(key, "LOGOUT", Duration.ofSeconds(expiration));
        }
    }

    public boolean isTokenLoggedOut(String token) {
        String key = "blacklist:" + token;
        return redisTemplate.opsForValue().get(key) != null; // Kiểm tra giá trị thay vì chỉ check key
    }
}
