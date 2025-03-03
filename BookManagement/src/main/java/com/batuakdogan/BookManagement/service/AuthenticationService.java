package com.batuakdogan.BookManagement.service;

import com.batuakdogan.BookManagement.model.User;
import com.batuakdogan.BookManagement.dto.auth.AuthResponse;
import com.batuakdogan.BookManagement.dto.auth.LoginRequest;
import com.batuakdogan.BookManagement.dto.auth.RegisterRequest;
import com.batuakdogan.BookManagement.repository.UserRepository;
import com.batuakdogan.BookManagement.security.JwtTokenProvider;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        log.info("Attempting to register user: {}", request.getUsername());
        
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Username already exists: {}", request.getUsername());
            throw new EntityExistsException("Username already exists");
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            user.setEmail(request.getEmail());
            
            log.debug("Saving new user to database");
            try {
                user = userRepository.save(user);
            } catch (Exception e) {
                log.error("Database error during user save: {}", e.getMessage(), e);
                throw new RuntimeException("Database error during registration", e);
            }
            
            log.debug("Generating JWT token for new user");
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                String jwtToken = jwtService.generateToken(authentication);
                log.info("Successfully registered user: {}", request.getUsername());
                return new AuthResponse(jwtToken, user.getId(), user.getUsername(), user.getEmail());
            } catch (Exception e) {
                log.error("JWT generation error: {}", e.getMessage(), e);
                throw new RuntimeException("Error generating authentication token", e);
            }
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new RuntimeException("Error during registration: " + e.getMessage(), e);
        }
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
            
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                    
            String jwtToken = jwtService.generateToken(authentication);
            
            log.info("Successfully authenticated user: {}", request.getUsername());
            return new AuthResponse(jwtToken, user.getId(), user.getUsername(), user.getEmail());
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException("Invalid username or password");
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            throw new RuntimeException("Error during login: " + e.getMessage());
        }
    }
} 