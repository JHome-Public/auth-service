package com.jhome.auth.jwt;

import com.jhome.auth.exception.CustomException;
import com.jhome.auth.response.ApiResponseCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final JwtProperty jwtProperty;

    public JwtUtil(JwtProperty jwtProperty) {
        this.jwtProperty = jwtProperty;
        this.secretKey = new SecretKeySpec(
                jwtProperty.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public void isExpired(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration();
        } catch (ExpiredJwtException e) {
            throw new CustomException(ApiResponseCode.INVALID_TOKEN);
        }
    }

    public String createJwt(String category, String username, String role, Long age) {
        return Jwts.builder()
                .claim("category", category)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + age))
                .signWith(secretKey)
                .compact();
    }

    public String createAccessToken(String username, String role) {
        return createJwt(
                jwtProperty.getAccessKey(),
                username,
                role,
                jwtProperty.getAccessAgeMS()
        );
    }

    public String createRefreshToken(String username, String role) {
        return createJwt(jwtProperty.getRefreshKey(),
                username,
                role,
                jwtProperty.getRefreshAgeMS());
    }
}