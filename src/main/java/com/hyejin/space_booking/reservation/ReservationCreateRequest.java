package com.hyejin.space_booking.reservation;

import jakarta.validation.constraints.*;

public record ReservationCreateRequest(
        @NotNull Long userKey,          // 예약자 회원 key
        @NotNull Long spaceId,          // 공간 key

        @Pattern(regexp = "\\d{8}", message = "visitDate는 yyyyMMdd 형식의 8자리 숫자여야 합니다.")
        @NotBlank String visitDate,
        @Pattern(regexp = "\\d{2}", message = "startTime은 HH 형식이어야 합니다.")
        @NotBlank String startTime,
        @Pattern(regexp = "\\d{2}", message = "endTime은 HH 형식이어야 합니다.")
        @NotBlank String endTime,
        @Min(value = 1, message = "최소 1명 이상이어야 합니다.")
        @NotNull Integer guestCount,

        String requestNote,             // 예약메시지
        String resvPhone,               // 예약자 휴대폰
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        @NotNull Integer amount         // 최종금액
) {}
