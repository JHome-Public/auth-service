package com.jhome.auth.security;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.service.RedisTokenService;
import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.response.ApiResponseCode;
import com.jhome.auth.response.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final RedisTokenService tokenService;
    private final JwtProperty jwtProperty;
    private final ResponseUtil responseUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            if(!request.getRequestURI().startsWith("/logout")) {
                filterChain.doFilter(request, response);
                return;
            }

            log.info("[LOGOUT_FILTER] Logout Process Start");
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(jwtProperty.getRefreshKey())) {
                    String refreshToken = cookie.getValue();
                    tokenService.deleteRefresh(refreshToken);
                    log.info("[LOGOUT_FILTER] Removed Stored Token");
                }
            }

            responseUtil.addCookie(response, jwtProperty.getRefreshKey(), "", 0L);
            log.info("[LOGOUT_FILTER] Removed Refresh Token");

            responseUtil.addHeader(response, jwtProperty.getAccessKey(), "");
            log.info("[LOGOUT_FILTER] Removed Access Token");

            responseUtil.setResponse(response, HttpStatus.OK, ApiResponse.success());
            log.info("[LOGOUT_FILTER] Logout Process Complete");
        } catch (Exception e) {
            log.error("[LOGOUT_FILTER] Logout Fail");
            responseUtil.setResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ApiResponse.fail(ApiResponseCode.FAIL));
            e.printStackTrace();
        }
    }
}
