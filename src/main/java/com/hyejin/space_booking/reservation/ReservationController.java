package com.hyejin.space_booking.reservation;

import com.hyejin.space_booking.ApiResponse;
import com.hyejin.space_booking.payment.PaymentReadyRequest;
import com.hyejin.space_booking.payment.PaymentReadyResponse;
import com.hyejin.space_booking.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예약/결제 플로우
 *
 * 1. 예약 생성
 *    - POST /api/reservations
 *    - 요청: ReservationCreateRequest
 *    - 응답: resvId (예약 ID)
 *
 * 2. 결제 준비 (결제하기 버튼 클릭 시)
 *    - POST /api/payments/ready
 *    - 요청: PaymentReadyRequest { resvId, pgProvider, method }
 *    - 응답: PaymentReadyResponse { merchantUid, amount, successUrl, failUrl ... }
 *
 * 3. PG 결제 진행 (프론트에서 PG SDK/결제창 호출)
 *
 * 4. 결제 확정
 *    - POST /api/payments/confirm
 *    - 요청: PaymentConfirmRequest { orderId(=merchantUid), ... }
 *    - 응답: PaymentConfirmResponse
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    /** 예약 생성 */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Long>> createReservation(@Valid @RequestBody ReservationCreateRequest req) {
        Long resvId = reservationService.createPendingReservation(req);
        return ResponseEntity.ok(ApiResponse.success(resvId));
    }

}
