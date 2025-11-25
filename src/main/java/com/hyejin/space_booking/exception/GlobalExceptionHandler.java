package com.hyejin.space_booking.exception;

import com.hyejin.space_booking.api.ApiResponse;
import com.hyejin.space_booking.common.ApiException;
import com.hyejin.space_booking.common.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApi(ApiException ex) {
        var ec = ex.errorCode;
        return ResponseEntity.status(ec.status)
                .body(ApiResponse.error(ec.code, ex.getMessage()));
    }

   /* @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        String msg = fieldError != null ? fieldError.getDefaultMessage() : ErrorCode.BAD_REQUEST.defaultMsg;
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.status)
                .body(ApiResponse.error(ErrorCode.BAD_REQUEST.code, msg));
    }
    */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {

        var fieldErrors = ex.getBindingResult().getFieldErrors();

        // 1) 필수값 누락 에러 우선 처리
        for (FieldError error : fieldErrors) {
            if (isRequiredError(error)) {
                String msg = "필수값이 없습니다 [" + error.getField() + "]";
                return ResponseEntity
                        .status(ErrorCode.BAD_REQUEST.status)
                        .body(ApiResponse.error(
                                ErrorCode.BAD_REQUEST.code,
                                msg
                        ));
            }
        }

        // 2) 필수값은 채워짐 → 일반 유효성 검증 에러 하나만 리턴 (기존 로직 유지)
        var fieldError = ex.getBindingResult().getFieldError();
        String msg = fieldError != null
                ? fieldError.getDefaultMessage()
                : ErrorCode.BAD_REQUEST.defaultMsg;

        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.status)
                .body(ApiResponse.error(
                        ErrorCode.BAD_REQUEST.code,
                        msg
                ));
    }

    private boolean isRequiredError(FieldError error) {
        String code = error.getCode();
        return "NotNull".equals(code)
                || "NotBlank".equals(code)
                || "NotEmpty".equals(code);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleEtc(Exception ex) {
        ex.printStackTrace(); // 콘솔에 로그
        return ResponseEntity.internalServerError()
                .body(ApiResponse.error("INTERNAL_ERROR", ex.getMessage()));
    }
}
