package com.example.auth_service.config;

import com.example.auth_service.service.CustomUserDetailsService;
import com.example.auth_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // Expose actuator health and info endpoints
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(EndpointRequest.to(HealthEndpoint.class, InfoEndpoint.class)).permitAll()
                // Other actuator endpoints if needed
                .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()

                // Auth endpoints
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register-superadmin").permitAll()
                .requestMatchers("/api/auth/register").hasAuthority("ROLE_SUPERADMIN")
                .requestMatchers("/api/auth/whoami").authenticated()
                .requestMatchers("/api/auth/test-protected").authenticated()
                .requestMatchers("/api/auth/change-password").authenticated()
                .requestMatchers("/api/auth/admin/change-password").hasRole("SUPERADMIN")

                // User management
                .requestMatchers("/api/auth/admin/create-user").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/users/superadmin/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/users/admin/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/users/user/**").hasAnyRole("ADMIN", "SUPERADMIN")

                // Department endpoints
                .requestMatchers(HttpMethod.POST,   "/api/departments").hasRole("SUPERADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/departments/**").hasRole("SUPERADMIN")
                .requestMatchers("/api/departments/assign").hasAnyRole("ADMIN", "SUPERADMIN")
                .requestMatchers("/api/departments/unassign").hasAnyRole("ADMIN", "SUPERADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
