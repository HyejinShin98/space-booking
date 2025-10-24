package com.hyejin.space_booking.api;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
            String code,
            String message,
            @JsonInclude(JsonInclude.Include.NON_NULL) T data
    ) {

    // 성공 응답 (메시지 + 데이터)
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>("0000", msg, data);
    }
    // 성공 응답 (데이터만)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0000", "success", data);
    }
    // 성공 응답 (코드만)
    public static ApiResponse<Void> success() {
        return new ApiResponse<>("0000", "success", null);
    }
    // 에러 응답
    public static ApiResponse<Void> error(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

}
