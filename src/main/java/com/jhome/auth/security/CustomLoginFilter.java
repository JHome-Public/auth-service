package com.jhome.auth.security;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.jwt.JwtUtil;
import com.jhome.auth.service.RedisTokenService;
import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.response.ApiResponseCode;
import com.jhome.auth.response.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RedisTokenService tokenService;
    private final ResponseUtil responseUtil;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[SecurityLoginFilter] Start Login");
        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        log.info("[SecurityLoginFilter] Login Success");
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // 토큰 발급
        String access = jwtUtil.createAccessToken(username, role);
        String refresh = jwtUtil.createRefreshToken(username, role);
        log.info("[SecurityLoginFilter] Create Token Complete");

        // 리프레시 토큰 저장
        tokenService.saveRefresh(username, refresh);
        log.info("[SecurityLoginFilter] Save Token Complete");

        // 응답 반환
        responseUtil.addHeader(response, jwtProperty.getAccessKey(), jwtProperty.getPrefix() + access);
        responseUtil.addCookie(response, jwtProperty.getRefreshKey(), refresh, jwtProperty.getRefreshAgeMS());
        responseUtil.setResponse(
                response,
                HttpStatus.OK,
                ApiResponse.success());
        log.info("[SecurityLoginFilter] End Login Process");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("[SecurityLoginFilter] Login Fail: {}", failed.getMessage());
        responseUtil.setResponse(
                response,
                HttpStatus.BAD_REQUEST,
                ApiResponse.fail(ApiResponseCode.LOGIN_FAILURE));
    }

}
