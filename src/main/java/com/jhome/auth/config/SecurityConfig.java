package com.jhome.auth.config;

import com.jhome.auth.jwt.JwtProperty;
import com.jhome.auth.security.CustomLoginFilter;
import com.jhome.auth.security.CustomLogoutFilter;
import com.jhome.auth.security.CustomOAuth2LoginHandler;
import com.jhome.auth.jwt.JwtUtil;
import com.jhome.auth.service.RedisTokenService;
import com.jhome.auth.response.ResponseUtil;
import com.jhome.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomOAuth2LoginHandler oAuth2Handler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RedisTokenService tokenService;
    private final ResponseUtil responseUtil;
    private final JwtUtil jwtUtil;
    private final JwtProperty jwtProperty;

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        // cors 설정은 추후 진행
//        http.cors(corsCustomizer -> corsCustomizer
//                .configurationSource(new CorsConfigurationSource() {
//                    @Override
//                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//                        CorsConfiguration configuration = new CorsConfiguration();
//
//                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
//                        configuration.setAllowedMethods(Collections.singletonList("*"));
//                        configuration.setAllowCredentials(true);
//                        configuration.setAllowedHeaders(Collections.singletonList("*"));
//                        configuration.setMaxAge(3600L);
//
//                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
//                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
//
//                        return configuration;
//                    }
//                })
//        );

        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(oAuth2Handler)
                .failureHandler(oAuth2Handler));

        http.addFilterAt(new CustomLoginFilter(
                authenticationManager(authenticationConfiguration),
                tokenService,
                responseUtil,
                jwtUtil,
                jwtProperty
        ), UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new CustomLogoutFilter(
                tokenService,
                jwtProperty,
                responseUtil
        ), LogoutFilter.class);

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/**").permitAll()
                .anyRequest().authenticated());

        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
