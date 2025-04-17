package com.jhome.auth.feign;

import com.jhome.auth.response.ApiResponse;
import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.dto.UserEntityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping(UserUri.DEFAULT)
    ResponseEntity<ApiResponse<UserEntityResponse>> join(UserEntityRequest userEntityRequest);

    @GetMapping(UserUri.DEFAULT + UserUri.USERNAME_VARIABLE)
    ResponseEntity<ApiResponse<UserEntityResponse>> getDetail(@PathVariable String username);

}
