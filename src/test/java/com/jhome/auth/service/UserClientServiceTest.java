package com.jhome.auth.service;

import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.feign.UserClient;
import com.jhome.auth.exception.CustomException;
import com.jhome.auth.response.ApiResponseCode;
import com.jhome.auth.dto.UserEntityResponse;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserClientServiceTest {

    @InjectMocks
    private UserClientService userClientService;

    @Mock
    private UserClient userClient;

    @Test
    void testGetDetail_Success() {
        // given
        final String username = "username";
        final UserEntityResponse userEntityResponse = getUserEntityResponse();
        final ResponseEntity<ApiResponse<UserEntityResponse>> response = ResponseEntity.ok(
                (ApiResponse<UserEntityResponse>) ApiResponse.success(userEntityResponse));

        when(userClient.getDetail(anyString())).thenReturn(response);

        // when
        UserEntityResponse result = userClientService.getUserDetail(username);

        // then
        assertNotNull(result);
    }

    @Test
    void testGetDetail_Fail() {
        // given
        final String username = "123";

        FeignException.BadRequest feignBadRequestException = mock(FeignException.BadRequest.class);
        when(userClient.getDetail(anyString())).thenThrow(feignBadRequestException);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userClientService.getUserDetail(username);
        });

        // then
        assertEquals(ApiResponseCode.LOGIN_FAILURE, exception.getApiResponseCode());
    }

    @Test
    void testAddUser_Success() {
        // given
        final UserEntityRequest userEntityRequest = getUserEntityRequest();
        final UserEntityResponse userEntityResponse = getUserEntityResponse();
        final ResponseEntity<ApiResponse<UserEntityResponse>> response = ResponseEntity.ok(
                (ApiResponse<UserEntityResponse>) ApiResponse.success(userEntityResponse));

        when(userClient.join(any(UserEntityRequest.class))).thenReturn(response);

        // when
        UserEntityResponse result = userClientService.addUser(userEntityRequest);

        // then
        assertNotNull(result);
    }

    @Test
    void testAddUser_Fail() {
        // given
        final UserEntityRequest userEntityRequest = getUserEntityRequest();

        FeignException.BadRequest feignBadRequestException = mock(FeignException.BadRequest.class);
        when(userClient.join(any(UserEntityRequest.class))).thenThrow(feignBadRequestException);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userClientService.addUser(userEntityRequest);
        });

        // then
        assertEquals(ApiResponseCode.LOGIN_FAILURE, exception.getApiResponseCode());
    }

    private static UserEntityRequest getUserEntityRequest() {
        return UserEntityRequest.builder()
                .username("oauthUser")
                .password("given02!")
                .name("aAuthUser")
                .email("oauthUser@email.com")
                .userType(2)
                .build();
    }

    private static UserEntityResponse getUserEntityResponse() {
        return UserEntityResponse.builder()
                .id(1L)
                .username("oauthUser")
                .password("given02!endcoded")
                .role("ROLE_USER")
                .type("OAUTH")
                .name("aAuthUser")
                .email("oauthUser@email.com")
                .phone("")
                .picture("")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
    }

}