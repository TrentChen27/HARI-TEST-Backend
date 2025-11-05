package com.pitt.hari_exercise_tracker.config;

import com.pitt.hari_exercise_tracker.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Automatically creates a constructor for all final fields
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. Check if the header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Let the request proceed
            return;
        }

        // 3. Extract the token (everything after "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Extract the username from the token
        final String username = jwtService.extractUsername(jwt);

        // 5. Check if we have a username AND the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Load the user from the database (using our "phonebook")
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. Check if the token is valid (matches the user and is not expired)
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. If valid, create an "authentication token" (the "golden ticket")
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // We don't need credentials
                        userDetails.getAuthorities() // This is where "ROLE_USER" is used!
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Set this "golden ticket" in the Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}