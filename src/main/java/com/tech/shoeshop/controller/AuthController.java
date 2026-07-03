package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.dto.register.RegisterRequest;
import com.tech.shoeshop.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     *
     * @param request registration information
     * @return success response
     */
    @PostMapping("/register")
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request){

        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "User registered successfully"));
    }
}
