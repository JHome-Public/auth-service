package com.jhome.auth.service;

import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.dto.UserEntityResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private UserClientService userClientService;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private OAuth2User oAuth2User;

    @Mock
    private UserEntityResponse userEntityResponse;

    @Test
    void testLoadUser_UserFound() {
        // given
        when(oAuth2User.getName()).thenReturn("testUser");
        when(userClientService.getUserDetail("testUser")).thenReturn(userEntityResponse);

        CustomOAuth2UserService spyService = spy(customOAuth2UserService);
        doReturn(oAuth2User).when(spyService).fetchOAuth2User(any(OAuth2UserRequest.class));

        // when
        OAuth2User result = spyService.loadUser(userRequest);

        // then
        assertNotNull(result);
        verify(userClientService, times(1)).getUserDetail("testUser");
    }

    @Test
    void testLoadUser_UserNotFound() {
        // given
        when(userClientService.getUserDetail("newUser")).thenThrow(new RuntimeException("User not found"));
        when(userClientService.addUser(any(UserEntityRequest.class))).thenReturn(userEntityResponse);

        CustomOAuth2UserService spyService = spy(customOAuth2UserService);
        doReturn(oAuth2User).when(spyService).fetchOAuth2User(any(OAuth2UserRequest.class));

        // when
        OAuth2User result = spyService.loadUser(userRequest);

        // then
        assertNotNull(result);
        verify(userClientService, times(1)).addUser(any(UserEntityRequest.class));
    }
}
