package com.hyejin.space_booking.space;

import com.hyejin.space_booking.global.PageResponse;
import com.hyejin.space_booking.common.exception.ApiException;
import com.hyejin.space_booking.common.ErrorCode;
import com.hyejin.space_booking.common.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpaceService {
    private final SpaceRepository spaceRepository;

    /**
     * 공간검색 리스트
     */
    public PageResponse<SpaceSearchResponse> search(SpaceSearchRequest req) {
        // 1) Pageable
        Pageable pageable = PageUtils.resolvePageable(req, Sort.unsorted());

        // 2) 조회
        Page<SpaceRepository.Row> page = spaceRepository.findSpacesBySearchCondition(
                req.keyword(), req.option(), req.dayOfWeek(), req.time(),
                req.capacity(), req.sort(), pageable
        );

        // 3) 매핑 + 래핑
        return PageResponse.of(page.map(this::toDto));
    }

    private SpaceSearchResponse toDto(SpaceRepository.Row r) {
        return new SpaceSearchResponse(
                n2L(r.getSpaceId()),
                r.getTitle(),
                r.getAddress(),
                r.getImageUrl(),
                n2I(r.getCapacity()),
                r.getMinPriceNumeric(),
                r.getMinPrice(),
                r.getRegDate()
        );
    }

    private static Long n2L(Number n) { return n == null ? null : n.longValue(); }
    private static Integer n2I(Number n) { return n == null ? null : n.intValue(); }


    /**
     * 공간 상세정보 조회
     */
    public SpaceInfoResponse info(SpaceInfoRequest req) {
        Long spaceId = req.spaceId();
        return SpaceInfoResponse.from(
                spaceRepository.findSpace(spaceId)
                        .orElseThrow(() -> new ApiException(ErrorCode.SPACE_NOT_FOUND))
        );
    }

    /**
     * 공간 요일별 예약 상태 조회
     */
    public SpaceInfoResponse reservationSchedule(SpaceInfoRequest req) {
        Long spaceId = req.spaceId();
        return SpaceInfoResponse.from(
                spaceRepository.findSpace(spaceId)
                        .orElseThrow(() -> new ApiException(ErrorCode.SPACE_NOT_FOUND))
        );
    }

    
}


