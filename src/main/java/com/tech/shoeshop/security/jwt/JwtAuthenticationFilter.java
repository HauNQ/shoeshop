package com.tech.shoeshop.security.jwt;

import com.tech.shoeshop.service.impl.auth.MyUserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MyUserDetailService myUserDetailService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            if(SecurityContextHolder.getContext().getAuthentication() == null){
                String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

                if(authHeader != null && authHeader.startsWith("Bearer ")){
                    String token = authHeader.substring(7).trim();
                    String username = jwtService.extractUsername(token);

                    if(username != null){
                        UserDetails userDetail = myUserDetailService.loadUserByUsername(username);

                        if(jwtService.validateToken(token, userDetail)){
                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(
                                            createAuthentication(userDetail, request)
                                    );
                        }
                    }
                }
            }
        }catch (ExpiredJwtException ex) {
            log.warn("JWT has expired: {}", ex.getMessage());
            request.setAttribute("jwt_error", "JWT has expired");
        }
        catch (SignatureException ex) {
            log.warn("Invalid JWT signature: {}", ex.getMessage());
            request.setAttribute("jwt_error", "Invalid JWT signature");
        }
        catch (JwtException ex) {
            log.warn("Invalid JWT: {}", ex.getMessage());
            request.setAttribute("jwt_error", "Invalid JWT");
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken createAuthentication(UserDetails userDetails,
                                                                     HttpServletRequest request){
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        return authentication;
    }
}
