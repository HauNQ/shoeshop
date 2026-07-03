package com.tech.shoeshop.common.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {
    private final int status;
    private final String message;
    private final Map<String, String> errors;
    private final LocalDateTime timestamp;
}
