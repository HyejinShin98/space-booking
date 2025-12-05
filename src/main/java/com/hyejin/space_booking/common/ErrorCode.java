package com.hyejin.space_booking.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    /* 회원 */
    DUPLICATE_USER_ID("E0001", HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL("E0002", HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    SOCIAL_ACCOUNT_EXISTS("E0003", HttpStatus.CONFLICT, "소셜로 가입된 이메일입니다."),
    USER_NOT_FOUND("E1004", HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    LOGIN_DATA_NOT_FOUND("E1005", HttpStatus.NOT_FOUND, "로그인 정보를 찾을 수 없습니다."),
    PW_INVALID_CREDENTIAL("E1006", HttpStatus.NOT_FOUND, "비밀번호가 일치하지 않습니다."),
    SOCIAL_ACCOUNT_LOGIN_REQ("E1007", HttpStatus.CONFLICT, "소셜 로그인을 이용해주세요."),

    /* 공간 */
    SPACE_NOT_FOUND("E2001", HttpStatus.NOT_FOUND, "공간 정보를 찾을 수 없습니다."),
    
    /* 예약 */
    RESERVATION_NOT_FOUND("E3001", HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    RESERVATION_STATUS_NOT_PAYABLE("E3002", HttpStatus.CONFLICT, "결제를 할 수 없는 예약 진행상태입니다."),
    INVALID_TIME_RANGE("E3003",HttpStatus.BAD_REQUEST,"시작 시간이 종료시간보다 같거나 클 수 없습니다."),

    /* 공통*/
    REQUIRED_IS_NOT_FOUND("E0000", HttpStatus.BAD_REQUEST, "필수값이 없습니다."),
    BAD_REQUEST("E9999", HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");

    public final String code;
    public final HttpStatus status;
    public final String defaultMsg;
    ErrorCode(String code, HttpStatus status, String defaultMsg) {
        this.code = code; this.status = status; this.defaultMsg = defaultMsg;
    }
}
