package com.hyejin.space_booking.api.controller;

import com.hyejin.space_booking.api.ApiResponse;
import com.hyejin.space_booking.api.request.SpaceSearchRequest;
import com.hyejin.space_booking.api.response.PageResponse;
import com.hyejin.space_booking.api.response.SpaceSearchResponse;
import com.hyejin.space_booking.service.SpaceService;
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
    @PostMapping("/searchSpaces")
    public ResponseEntity<ApiResponse<PageResponse<SpaceSearchResponse>>> searchSpaces(
            @Valid @RequestBody SpaceSearchRequest req) {
        PageResponse<SpaceSearchResponse> resp = spaceService.searchSpaces(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }

    /**
     * 공간 상세정보 조회
     */
    /*@PostMapping("/info")
    public ResponseEntity<ApiResponse<SpaceInfoResponse>> info(@Valid @RequestBody SpaceInfoRequest req) {
        SpaceInfoResponse resp = spaceService.info(req);
        return ResponseEntity.ok(ApiResponse.success(resp));
    }*/


}
