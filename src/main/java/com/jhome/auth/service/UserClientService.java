package com.jhome.auth.service;

import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.feign.UserClient;
import com.jhome.auth.dto.UserEntityResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserClientService {

    private final UserClient userClient;

    @CircuitBreaker(name = "userClientBreaker", fallbackMethod = "addUserFallback")
    public UserEntityResponse addUser(final UserEntityRequest userEntityRequest){
        ResponseEntity<ApiResponse<UserEntityResponse>> response = userClient.join(userEntityRequest);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    public UserEntityResponse addUserFallback(final UserEntityRequest userEntityRequest, final Throwable throwable) {
        throw new UsernameNotFoundException(throwable.getMessage());
    }

    @CircuitBreaker(name = "userClientBreaker", fallbackMethod = "getUserDetailFallback")
    public UserEntityResponse getUserDetail(final String username){
        ResponseEntity<ApiResponse<UserEntityResponse>> response = userClient.getDetail(username);
        return Objects.requireNonNull(response.getBody()).getData();
    }

    public UserEntityResponse getUserDetailFallback(final String username, final Throwable throwable) {
        return UserEntityResponse.builder().build();
    }

}