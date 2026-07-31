package com.wbr.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${wbr.security.public-urls:#{null}}")
    private String[] publicUrls;

    // True only in the development profile (set via application-development.yaml).
    // Guards the /docs and /v3/api-docs paths so they are never open in production
    // even if springdoc ends up on the classpath.
    @Value("${springdoc.swagger-ui.enabled:false}")
    private boolean springdocEnabled;

    private final SecurityEntryPoint securityEntrypoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityEntrypoint)
                .accessDeniedHandler(securityEntrypoint)
        );

        if (StringUtils.hasText(issuerUri)) {
            return http
                    .authorizeHttpRequests(auth -> {
                        auth.requestMatchers("/actuator/**").permitAll();
                        if (springdocEnabled) {
                            // /docs is the entry point; springdoc redirects to /swagger-ui/** for assets
                            // and serves the OpenAPI spec from /v3/api-docs/**
                            auth.requestMatchers("/docs/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll();
                        }
                        if (publicUrls != null) {
                            auth.requestMatchers(publicUrls).permitAll();
                        }
                        auth.anyRequest().authenticated();
                    })
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                    .build();
        }

        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
}
