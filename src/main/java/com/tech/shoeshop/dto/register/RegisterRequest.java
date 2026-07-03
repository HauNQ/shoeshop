package com.tech.shoeshop.dto.register;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
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

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255,
            message = "Email must not exceed 255 characters")
    private String email;
}
