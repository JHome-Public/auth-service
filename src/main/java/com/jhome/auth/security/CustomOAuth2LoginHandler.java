package com.jhome.auth.security;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.jwt.JwtUtil;
import com.jhome.auth.service.RedisTokenService;
import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.response.ApiResponseCode;
import com.jhome.auth.response.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final RedisTokenService tokenService;
    private final ResponseUtil responseUtil;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;

    // 로그인 성공 시 처리
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("[OAuth2LoginHandler] Login Success");
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String username = authToken.getPrincipal().getAttribute("email");
        String role = authToken.getAuthorities().iterator().next().getAuthority();

        // 토큰 발급
        String access = jwtUtil.createAccessToken(username, role);
        String refresh = jwtUtil.createRefreshToken(username, role);
        log.info("[OAuth2LoginHandler] Create Token Complete");

        // 리프레시 토큰 저장
        tokenService.saveRefresh(username, refresh);
        log.info("[OAuth2LoginHandler] Save Token Complete");

        // 응답 반환
        responseUtil.addHeader(response, jwtProperty.getAccessKey(), jwtProperty.getPrefix() + access);
        responseUtil.addCookie(response, jwtProperty.getRefreshKey(), refresh, jwtProperty.getRefreshAgeMS());
        responseUtil.setResponse(
                response,
                HttpStatus.OK,
                ApiResponse.success());
        log.info("[OAuth2LoginHandler] End Login Process");
    }

    // 로그인 실패 시 처리
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("[OAuth2LoginHandler] Login Fail, {}", exception.getMessage());
        responseUtil.setResponse(
                response,
                HttpStatus.UNAUTHORIZED,
                ApiResponse.fail(ApiResponseCode.LOGIN_FAILURE));
    }
}
