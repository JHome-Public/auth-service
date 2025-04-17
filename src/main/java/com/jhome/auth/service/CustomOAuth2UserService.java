package com.jhome.auth.service;

import com.jhome.auth.domain.CustomOAuth2User;
import com.jhome.auth.dto.UserEntityRequest;
import com.jhome.auth.dto.UserEntityResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserClientService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = fetchOAuth2User(userRequest);
        log.info("[CustomOAuth2UserService] OAuth2User Login Start");

        UserEntityResponse user = userService.getUserDetail(oAuth2User.getName());
        if(user == UserEntityResponse.builder().build()){
            log.info("[CustomOAuth2UserService] OAuth2User Not Found");
            UserEntityRequest userEntityRequest = UserEntityRequest.build(oAuth2User);
            user = userService.addUser(userEntityRequest);
            log.info("[CustomOAuth2UserService] OAuth2User Created");
        }

        log.info("[CustomOAuth2UserService] OAuth2User Login Success");
        return new CustomOAuth2User(user);
    }

    protected OAuth2User fetchOAuth2User(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }
}

