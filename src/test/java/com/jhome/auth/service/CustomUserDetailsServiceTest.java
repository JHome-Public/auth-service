package com.jhome.auth.service;

import com.jhome.auth.dto.UserEntityResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserClientService userClientService;

    @Test
    void testLoadUserByUsername_Success() {
        // given
        final String username = "testUser";
        final UserEntityResponse userEntityResponse = getUserEntityResponse();

        when(userClientService.getUserDetail(username)).thenReturn(userEntityResponse);

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // then
        assertNotNull(userDetails);
        assertEquals(userEntityResponse.getUsername(), userDetails.getUsername());
        assertEquals(userEntityResponse.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(userEntityResponse.getRole())));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // given
        final String username = "notExistUser";

        when(userClientService.getUserDetail(username)).thenThrow(new UsernameNotFoundException("User not found"));

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    private static UserEntityResponse getUserEntityResponse() {
        return UserEntityResponse.builder()
                .id(1L)
                .username("testUser")
                .password("encodedPassword")
                .role("ROLE_USER")
                .type("OAUTH")
                .name("Test User")
                .email("testUser@email.com")
                .phone("")
                .picture("")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status("ACTIVE")
                .build();
    }
}
