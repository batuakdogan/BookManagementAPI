package com.batuakdogan.BookManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            log.info("Request URI: {}", request.getRequestURI());
            log.info("JWT from request: {}", jwt != null ? "present" : "not present");
            log.info("Full Authorization header: {}", request.getHeader("Authorization"));

            if (StringUtils.hasText(jwt)) {
                boolean isValid = tokenProvider.validateToken(jwt);
                log.info("Token validation result: {}", isValid);
                
                if (isValid) {
                    Long userId = tokenProvider.getUserIdFromJWT(jwt);
                    log.info("User ID from JWT: {}", userId);

                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    log.info("User loaded: {}, Authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());
                    
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("Authentication set in SecurityContext for user: {}", userDetails.getUsername());
                } else {
                    log.warn("Invalid JWT token");
                }
            } else {
                log.warn("No JWT token found in request");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            log.error("Exception details:", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Raw Authorization header: {}", bearerToken);
        
        if (StringUtils.hasText(bearerToken)) {
            // Handle case where "Bearer" appears twice
            if (bearerToken.startsWith("Bearer Bearer ")) {
                String token = bearerToken.substring(14); // Remove both "Bearer " strings
                log.info("Removed double Bearer prefix, extracted token: {}", token);
                return token;
            } else if (bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                log.info("Extracted token: {}", token);
                return token;
            }
            // If the token doesn't start with Bearer, log it
            log.warn("Authorization header doesn't start with 'Bearer '");
        } else {
            log.warn("Authorization header is empty or null");
        }
        return null;
    }
} 