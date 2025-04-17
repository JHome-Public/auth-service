package com.jhome.auth.service;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;

    public void saveRefresh(String username, String token) {
        String key = "username:" + username;
        redisTemplate.opsForHash().put(key, jwtProperty.getRefreshKey(), token);
        redisTemplate.expire(key, Duration.ofMillis(jwtProperty.getRefreshAgeMS()));
    }

    public void deleteRefresh(String token) {
        String username = jwtUtil.getUsername(token);
        String key = "username:" + username;
        redisTemplate.delete(key);
    }
}
