package com.tech.shoeshop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min=3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9_]+$",
            message = "Username may contain only letter, numbers, and underscores"
    )
    private String username;

    @Size(min = 8, max = 100, message = "Password must be 8-100 characters")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$",
            message = "Password must contain at least one letter and one number"
    )
    private String password;
}
