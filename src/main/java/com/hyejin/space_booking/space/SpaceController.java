package com.hyejin.space_booking.space;

import com.hyejin.space_booking.ApiResponse;
import com.hyejin.space_booking.payment.PaymentReadyRequest;
import com.hyejin.space_booking.global.PageResponse;
import com.hyejin.space_booking.payment.PaymentReadyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spaces")
public class SpaceController {
    private final SpaceService spaceService;

    /**
     * 공간 목록 조회 (페이징)
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<SpaceSearchResponse>>> search(
            @Valid @RequestBody SpaceSearchRequest req) {
        PageResponse<SpaceSearchResponse> resp = spaceService.search(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    /**
     * 공간 상세정보 조회
     */
    @PostMapping("/info")
    public ResponseEntity<ApiResponse<SpaceInfoResponse>> info(@Valid @RequestBody SpaceInfoRequest req) {
        SpaceInfoResponse resp = spaceService.info(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    /**
     * 공간 요일별 예약상태 조회
     */
    /*@PostMapping("/reservation-schedule")
    public ResponseEntity<ApiResponse<SpaceInfoResponse>> reservationSchedule(@Valid @RequestBody SpaceReservationScheduleRequest req) {
        SpaceInfoResponse resp = spaceService.reservationSchedule(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
        return null;
    }*/

    /**
     * 공간 예약하기
     */
    @PostMapping("/reservation")
    public ResponseEntity<ApiResponse<PaymentReadyResponse>> reservation(@Valid @RequestBody PaymentReadyRequest req) {
//        SpaceInfoResponse resp = spaceService.reservation(req);
//        return ResponseEntity.ok(ApiResponse.success(resp));
        return null;
    }



}
