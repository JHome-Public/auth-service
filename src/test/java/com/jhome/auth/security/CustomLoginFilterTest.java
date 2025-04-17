package com.jhome.auth.security;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.jwt.JwtUtil;
import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.response.ApiResponseCode;
import com.jhome.auth.response.ResponseUtil;
import com.jhome.auth.service.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomLoginFilterTest {

    @InjectMocks
    private CustomLoginFilter customLoginFilter;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisTokenService tokenService;

    @Mock
    private ResponseUtil responseUtil;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtProperty jwtProperty;


    @Test
    void testAttemptAuthentication() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getParameter("username")).thenReturn("testUser");
        when(request.getParameter("password")).thenReturn("password");

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken("testUser", "password", null);
        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);

        Authentication authentication = customLoginFilter.attemptAuthentication(request, response);
        assertNotNull(authentication);
    }

    @Test
    void testSuccessfulAuthentication() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testUser");
//        when(authentication.getAuthorities()).thenReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        customLoginFilter.successfulAuthentication(request, response, chain, authentication);

        verify(responseUtil).addHeader(response, jwtProperty.getAccessKey(), "Bearer " + "accessToken");
        verify(responseUtil).addCookie(response, jwtProperty.getRefreshKey(), "refreshToken", jwtProperty.getRefreshAgeMS());
    }

    @Test
    void testUnsuccessfulAuthentication() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = mock(AuthenticationException.class);

        customLoginFilter.unsuccessfulAuthentication(request, response, exception);
        verify(responseUtil).setResponse(response, HttpStatus.UNAUTHORIZED, ApiResponse.fail(ApiResponseCode.LOGIN_FAILURE));
    }
}
