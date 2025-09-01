package com.madebawojo.nysc.ppa.clearance.config;

import com.madebawojo.nysc.ppa.clearance.security.CustomAccessDeniedHandler;
import com.madebawojo.nysc.ppa.clearance.security.CustomAuthenticationEntryPoint;
import com.madebawojo.nysc.ppa.clearance.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(PathRequest.toH2Console()).permitAll() // Allow H2 console access
//                                .requestMatchers("/api/v1/ppas/**").permitAll()
//                                .requestMatchers("/api/v1/super-admin/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .anyRequest().authenticated() // Secure other requests
                )
                .csrf(csrf -> csrf
                                .ignoringRequestMatchers("/api/**")
//                        .ignoringRequestMatchers("/api/v1/auth/**") // Disable CSRF for /auth routes as well
                                .ignoringRequestMatchers(PathRequest.toH2Console()) // Disable CSRF for H2 console
//                                .ignoringRequestMatchers("/api/v1/ppas/**")
//                                .ignoringRequestMatchers("/api/v1/super-admin/**")

//                        .ignoringRequestMatchers("/api/v1/auth/**") // Disable CSRF for /auth routes as well
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Allow H2 console in same-origin frame
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 401
                        .accessDeniedHandler(customAccessDeniedHandler)           // 403
                );
        return http.build();
    }
}