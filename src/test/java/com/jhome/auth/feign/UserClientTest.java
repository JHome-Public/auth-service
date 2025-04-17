package com.jhome.auth.feign;

import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.dto.UserEntityResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Test
    void contextLoads() {
    }

    @Test
    void userClient_join_connectionTest() {
        // given
        UserEntityRequest userEntityRequest = UserEntityRequest.builder()
                .username("newUser")
                .password("password123")
                .email("newUser@email.com")
                .build();

        // when
        ResponseEntity<ApiResponse<UserEntityResponse>> response = userClient.join(userEntityRequest);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newUser", response.getBody().getData().getUsername());
        assertEquals("OAUTH", response.getBody().getData().getType());
    }

    @Test
    void testClient_getDetail() {
        // given

        // when
        final ResponseEntity<ApiResponse<UserEntityResponse>> result = userClient.getDetail("oauthUser");

        // then
        assertNotNull(result);
        System.out.println(result);
    }

}
