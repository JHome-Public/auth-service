package com.jhome.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public final class UserEntityResponse {

    private Long id;
    private String username;
    private String password;
    private String role;
    private String type;
    private String name;
    private String email;
    private String phone;
    private String picture;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
}
