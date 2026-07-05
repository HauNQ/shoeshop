package com.tech.shoeshop.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    @Builder.Default
    private final String tokenType = "Bearer";

    private final String accessToken;

    private final String expiresIn;
}
