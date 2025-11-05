package com.pitt.hari_exercise_tracker.config;

import com.pitt.hari_exercise_tracker.repository.AppUserRepository;
import com.pitt.hari_exercise_tracker.repository.HealthCoachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <-- ADD IMPORT
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AppUserRepository appUserRepository;
    private final HealthCoachRepository healthCoachRepository;

    /**
     * This is the "Smart Phonebook"
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserDetails user = appUserRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return user;
            }
            user = healthCoachRepository.findByUsername(username).orElse(null);
            if (user != null) {
                return user;
            }
            throw new UsernameNotFoundException("User or Coach not found: " + username);
        };
    }

    /**
     * This is the "Data Access Object"
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // <-- Use the bean method
        return authProvider;
    }

    /**
     * This is unchanged and correct.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}