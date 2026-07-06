package com.tech.shoeshop.security.jwt;

import com.tech.shoeshop.common.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntrypoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = (String) request.getAttribute("jwt_error");

        if(message == null || message.isBlank()){
            message = "Authentication required";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message(message)
                        .errors(Map.of())
                        .build();

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
