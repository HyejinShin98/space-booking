package com.hyejin.space_booking.common;

public class ApiException extends RuntimeException{
    public final ErrorCode errorCode;
    public ApiException(ErrorCode errorCode) { super(errorCode.defaultMsg); this.errorCode = errorCode; }
    public ApiException(ErrorCode errorCode, String overrideMsg) { super(overrideMsg); this.errorCode = errorCode; }
}
