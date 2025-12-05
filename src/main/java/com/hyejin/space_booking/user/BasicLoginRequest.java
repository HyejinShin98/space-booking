package com.hyejin.space_booking.user;

import jakarta.validation.constraints.NotBlank;

/* 일반 로그인 항목 */
public record BasicLoginRequest(
        @NotBlank
        String userId,
        @NotBlank
        String userPw
) {}