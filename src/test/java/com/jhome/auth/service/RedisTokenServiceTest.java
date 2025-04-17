package com.jhome.auth.service;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisTokenServiceTest {

    @InjectMocks
    private RedisTokenService redisTokenService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtProperty jwtProperty;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private static final String USERNAME = "testUser";
    private static final String TOKEN = "mocked.jwt.token";
    private static final String REFRESH_KEY = "refresh_token";
    private static final long REFRESH_AGE_MS = 60000L;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        lenient().when(jwtProperty.getRefreshKey()).thenReturn(REFRESH_KEY);
        lenient().when(jwtProperty.getRefreshAgeMS()).thenReturn(REFRESH_AGE_MS);
        lenient().when(jwtUtil.getUsername(TOKEN)).thenReturn(USERNAME);
    }

    @Test
    void saveRefresh_shouldStoreTokenInRedis() {
        // Given
        String key = "username:" + USERNAME;

        // When
        redisTokenService.saveRefresh(USERNAME, TOKEN);

        // Then
        verify(hashOperations, times(1)).put(key, REFRESH_KEY, TOKEN);
        verify(redisTemplate, times(1)).expire(eq(key), any(Duration.class));
    }

    @Test
    void deleteRefresh_shouldRemoveTokenFromRedis() {
        // Given
        String key = "username:" + USERNAME;

        // When
        redisTokenService.deleteRefresh(TOKEN);

        // Then
        verify(redisTemplate, times(1)).delete(key);
    }

}