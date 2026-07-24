package com.tech.shoeshop.service.auth;

import com.tech.shoeshop.dto.request.auth.LoginRequest;
import com.tech.shoeshop.dto.request.auth.RegisterRequest;
import com.tech.shoeshop.dto.response.auth.LoginResponse;
import com.tech.shoeshop.entity.auth.User;

public interface AuthService {
    void register(RegisterRequest request);
    LoginResponse verify(LoginRequest request);
    User getCurrentUser();
}
