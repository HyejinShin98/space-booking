package com.hyejin.space_booking.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_USER_ID("E0001", HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL("E0002", HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    SOCIAL_ACCOUNT_EXISTS("E0003", HttpStatus.CONFLICT, "소셜로 가입된 이메일입니다."),

    BAD_REQUEST("E9999", HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    public final String code;
    public final HttpStatus status;
    public final String defaultMsg;
    ErrorCode(String code, HttpStatus status, String defaultMsg) {
        this.code = code; this.status = status; this.defaultMsg = defaultMsg;
    }
}
