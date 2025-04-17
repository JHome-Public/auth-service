package com.jhome.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@Builder
@ToString
public final class UserEntityRequest {

    private final String username;

    @ToString.Exclude
    private final String password;

    private final String name;

    private final String email;

    private final Integer userType;

    public static UserEntityRequest build(final OAuth2User oAuth2User){
        return UserEntityRequest.builder()
                .username(oAuth2User.getName())
                .password("oAuth2User")
                .name("oAuth2User")
                .email(oAuth2User.getName())
                .userType(2)
                .build();
    }
}
