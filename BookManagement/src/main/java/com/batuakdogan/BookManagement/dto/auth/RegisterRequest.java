package com.batuakdogan.BookManagement.dto.auth;

import com.batuakdogan.BookManagement.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for user registration")
public class RegisterRequest {
    @Schema(example = "john.doe", description = "Username for the new user")
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(example = "password123", description = "Password for the new user")
    @NotBlank(message = "Password is required")
    private String password;
    
    @Schema(example = "john.doe@example.com", description = "Email address for the new user")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Schema(example = "USER", description = "Role for the new user (USER, AUTHOR, or ADMIN)")
    @NotNull(message = "Role is required")
    private UserRole role;
} 