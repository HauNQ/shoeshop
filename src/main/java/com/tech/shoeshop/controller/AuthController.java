package com.tech.shoeshop.controller;

import com.tech.shoeshop.common.response.ApiResponse;
import com.tech.shoeshop.dto.request.auth.LoginRequest;
import com.tech.shoeshop.dto.request.auth.RegisterRequest;
import com.tech.shoeshop.dto.response.auth.LoginResponse;
import com.tech.shoeshop.service.auth.AuthService;
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

    /**
     * Authenticate user.
     *
     * @param username and password
     * @return success response
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> verify(@Valid @RequestBody LoginRequest request){

        LoginResponse response = authService.verify(request);

        return ResponseEntity.ok(
                        ApiResponse.success(
                                HttpStatus.OK,
                                "Authenticate user successfully",
                                response));
    }

    @GetMapping("/authen-test")
    public String testAuthen(){
        return "Test Authen Successfully";
    }
}
