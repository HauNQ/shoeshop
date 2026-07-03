package com.tech.shoeshop.common.response;

import lombok.*;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private final int status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(HttpStatusCode status, String message, T data){
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> success(HttpStatusCode status, String message){
        return success(status, message, null);
    }
}
