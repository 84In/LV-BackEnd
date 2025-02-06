package com.luanvan.apigateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String resource;

    @Value("${keycloak.public-client}")
    private boolean publicClient;

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
//        http
//                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
//                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.pathMatchers(PUBLIC_ENDPOINTS).permitAll()  // Cho phép các endpoint công khai
//                        .anyExchange().authenticated())
//                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.loginPage("/login"))
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder())));  // Cập nhật đây
//
//        return http.build();
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authorizeExchange(
                        authorizeExchangeSpec ->
                                authorizeExchangeSpec.pathMatchers("/auth/**", "/public/**").permitAll()  // Cho phép các endpoint công khai
                                .anyExchange().authenticated()
                )
                .oauth2Login(oAuth2LoginSpec -> oAuth2LoginSpec.loginPage("/auth/login"))  // Sử dụng OAuth2 login
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder())));  // Cấu hình Keycloak JWT

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/certs")
                .build();
    }

//    @Bean
//    public ReactiveJwtDecoder jwtDecoder() {
//        // Cấu hình NimbusReactiveJwtDecoder với jwkSetUri
//        return NimbusReactiveJwtDecoder.withJwkSetUri("http://localhost:8181/realms/spring-microservices-security-realm/protocol/openid-connect/certs")
//                .build();
//    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));  // Adjust according to your needs
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));

        // Register the configuration with a UrlBasedCorsConfigurationSource
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);  // Apply this configuration to all paths

        return source;  // Return the source instead of the configuration
    }

}
