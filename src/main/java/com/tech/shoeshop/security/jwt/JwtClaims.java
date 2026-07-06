package com.tech.shoeshop.security.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtClaims {
    public static final String AUTHORITIES = "authorities";
    public static final String EMAIL = "email";
    public static final String ISSUER = "shoe-shop";
}
