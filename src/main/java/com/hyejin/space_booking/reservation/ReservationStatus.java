package com.hyejin.space_booking.reservation;

/* 예약건 상태 */
public enum ReservationStatus {
    PENDING,   // 예약 요청됨(결제 대기중)
    CONFIRMED, // 결제 완료 → 예약 확정
    CANCELLED, // 유저 취소
    EXPIRED    // 결제중에 일정 시간지나 취소
}

