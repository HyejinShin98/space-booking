package com.hyejin.space_booking.api;

    public record ApiResponse<T>(
            String code,
            String message,
            T data
    ) {

    // data 포함
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("0000", message, data);
    }

    // data 없이
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("0000", message, null);
    }

    // 실패
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
