package org.kon.postr.config;

import org.kon.postr.security.CustomAuthenticationConverter;
import org.kon.postr.security.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true, // allows @Secured annotation
        jsr250Enabled = true // jakarta annotations for authorization, enable @RolesAllowed
//        prePostEnabled = true //  DEFAULT, enables @PreAuthorize, @PostAuthorize, @PreFilter and @PostFilter
)
public class SecurityConfig {

    private final CorsConfigurationSource configurationSource;
    private final CustomAuthenticationConverter customAuthenticationConverter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(CorsConfigurationSource configurationSource,
                          CustomAuthenticationConverter customAuthenticationConverter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.configurationSource = configurationSource;
        this.customAuthenticationConverter = customAuthenticationConverter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(configurationSource)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/users/**").hasRole("USER")
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
//                        .anyRequest().permitAll()
                )
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customAuthenticationConverter)
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

}
