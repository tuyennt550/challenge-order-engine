package com.price.orderengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;

    private ApiError error;

    public static <T> ApiResponse<T> success(
            T data
    ) {
        return new ApiResponse<>(
                data,
                null
        );
    }

    public static <T> ApiResponse<T> failure(
            String code,
            String message
    ) {
        return new ApiResponse<>(
                null,
                new ApiError(code, message)
        );
    }
}
