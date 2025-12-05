package com.hyejin.space_booking.common.exception;

import com.hyejin.space_booking.common.ErrorCode;

public class ApiException extends RuntimeException{
    public final ErrorCode errorCode;
    public ApiException(ErrorCode errorCode) { super(errorCode.defaultMsg); this.errorCode = errorCode; }
    public ApiException(ErrorCode errorCode, Object overrideMsg) { super((String) overrideMsg); this.errorCode = errorCode; }
}
