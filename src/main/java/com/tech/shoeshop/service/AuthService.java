package com.tech.shoeshop.service;

import com.tech.shoeshop.dto.register.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);
}
