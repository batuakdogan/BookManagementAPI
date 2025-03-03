package com.batuakdogan.BookManagement.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for user login")
public class LoginRequest {
    @Schema(example = "john.doe", description = "Username of the user")
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(example = "password123", description = "Password of the user")
    @NotBlank(message = "Password is required")
    private String password;
} 