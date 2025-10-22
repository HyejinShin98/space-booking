package com.hyejin.space_booking.api.request;

import jakarta.validation.constraints.NotBlank;

/* 일반 로그인 항목 */
public record BasicLoginRequest(
        @NotBlank(message = "아이디는 필수입니다.")
        String userId,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String userPw
) {}