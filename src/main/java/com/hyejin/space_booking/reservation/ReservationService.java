package com.hyejin.space_booking.reservation;


import com.hyejin.space_booking.common.ErrorCode;
import com.hyejin.space_booking.common.exception.ApiException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j

/* 예약 */
public class ReservationService {
    private final ReservationRepository reservationRepository;

    /**
     * 예약건 신규 생성
     */
    @Transactional
    public Long createPendingReservation(ReservationCreateRequest req) {
        Reservation reservation = Reservation.createPending(req);
        if (Integer.parseInt(req.startTime()) >= Integer.parseInt(req.endTime())) {
            throw new ApiException(ErrorCode.INVALID_TIME_RANGE);
        }
        Reservation saved = reservationRepository.save(reservation);
        return saved.getResvId();
    }


    /**
     * 결제 전 상태의 예약건만 조회
     * 조회된 예약건이 PENDDING 상태가 아닐경우 에러
     */
    // 결제 직전 상태(PENDING)인 예약만 가져오는 메소드
    @Transactional(readOnly = true)
    public Reservation getPendingReservation(Long reservationId) {

        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        // 2. 상태 검증
        if (!reservation.getStatus().equals(ReservationStatus.PENDING)) {
            throw new ApiException(ErrorCode.RESERVATION_STATUS_NOT_PAYABLE, reservation.getStatus());
        }

        // 3. 유효한 예약이면 반환
        return reservation;
    }

    
}


