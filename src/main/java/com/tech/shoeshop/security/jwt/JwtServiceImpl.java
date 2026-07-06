package com.tech.shoeshop.security.jwt;

import com.tech.shoeshop.model.MyUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private long ACCESS_TOKEN_EXPIRATION;

    /**
     * Generates a JWT access token for an authenticated user.
     *
     * @param authentication the authenticated user's Authentication object
     * @return a signed JWT access token
     */
    @Override
    public String generateToken(Authentication authentication) {
        Date now = new Date();
        Map<String, Object> claims = getClaims(authentication);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(authentication.getName())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION))
                .issuer(JwtClaims.ISSUER)
                .signWith(getKey())
                .compact();
    }

    private Map<String, Object> getClaims(Authentication authentication){
        Map<String, Object> claims = new HashMap<>();
        MyUserDetail myUserDetail = (MyUserDetail) authentication.getPrincipal();

        claims.put(JwtClaims.EMAIL, myUserDetail.getEmail());
        claims.put(JwtClaims.AUTHORITIES, authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );

        return claims;
    }

    private SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);

        return  claims.getSubject().equals(userDetails.getUsername())
                && claims.getExpiration().after(new Date())
                && claims.getIssuer().equals(JwtClaims.ISSUER)
                && userDetails.isAccountNonLocked()
                && userDetails.isAccountNonExpired()
                && userDetails.isCredentialsNonExpired()
                && userDetails.isEnabled();
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractClaim(token,Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }
}
