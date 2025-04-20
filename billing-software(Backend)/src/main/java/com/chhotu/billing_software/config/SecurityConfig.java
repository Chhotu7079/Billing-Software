package com.chhotu.billing_software.config;


import com.chhotu.billing_software.filter.JwtRequestFilter;
import com.chhotu.billing_software.service.impl.AppUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Injects custom UserDetailsService implementation
    private final AppUserDetailsServiceImpl appUserDetailsService;

    // Injects JWT filter that checks token in incoming requests
    private final JwtRequestFilter jwtRequestFilter;

//    Configures HTTP security: what is allowed, what is restricted, and how sessions are handled.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors(Customizer.withDefaults()) // Enables CORS with default configuration
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF protection (suitable for stateless REST APIs)
                .authorizeHttpRequests(auth -> auth
                        // These endpoints are accessible without authentication
                        .requestMatchers("/login", "/encode").permitAll()
                        // These endpoints require roles USER or ADMIN
                        .requestMatchers("/categories", "/items", "/orders", "/payments", "/dashboard").hasAnyRole("USER", "ADMIN")
                        // Only accessible by users with ADMIN role
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Any other request must be authenticated
                        .anyRequest().authenticated())
                // Session will not be created or used by Spring Security
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add custom JWT filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


//    Defines the password encoder bean used to hash and verify passwords.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    Defines the CORS filter bean to allow cross-origin requests.
    @Bean
    public CorsFilter corsFilter(){
        return new CorsFilter(corsConfigurationSource());
    }

//    CORS configuration setup: allows only specified origins, methods, and headers.
    private UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173")); // Frontend origin (e.g., React/Vite app)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // HTTP methods allowed
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Headers allowed in requests
        config.setAllowCredentials(true); // Allows sending credentials (cookies, auth headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS settings to all endpoints

        return source;
    }

    /**
     * Configures the AuthenticationManager with a DAO-based authentication provider.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(appUserDetailsService); // Set custom user service
        authProvider.setPasswordEncoder(passwordEncoder()); // Set password encoder
        return new ProviderManager(authProvider); // Return authentication manager
    }
}
