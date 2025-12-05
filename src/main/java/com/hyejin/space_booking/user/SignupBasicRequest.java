package com.hyejin.space_booking.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/* 회원가입 항목 */
public record SignupBasicRequest(

        @NotBlank
        String userId,
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$", message = "비밀번호는 영문+숫자 조합 8~20자리여야 합니다.")
        @NotBlank
        String userPw,
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank
        String email,
        @NotBlank
        String name,
        @Pattern(regexp = "^\\d{8}$", message = "생년월일은 yyyyMMdd 형식의 8자리 숫자여야 합니다.")
        @NotBlank
        String birthDate,
        @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자만 10~11자리여야 합니다.")
        @NotBlank
        String phoneNum
) {}