package com.pitt.hari_exercise_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This is the NEW, unified configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tell Spring Security to use our new CORS config (defined below)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF (still correct)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Disable form login and HTTP Basic authentication
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. Configure authorization rules (still correct)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers("/api/users/login-uuid").permitAll()
                        .requestMatchers("/api/coaches/register").permitAll()
                        .requestMatchers("/api/coaches/login").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * This NEW bean defines our CORS rules, copied from WebConfig.
     * SecurityConfig will now use this automatically.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Set our allowed origins
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8100", // For ionic serve
                "http://localhost:4200"  // For angular serve
        ));

        // 2. Set allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Set allowed headers
        configuration.setAllowedHeaders(List.of("*"));

        // 4. Allow credentials
        configuration.setAllowCredentials(true);

        // 5. Apply this configuration to all our API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}