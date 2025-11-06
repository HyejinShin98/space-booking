package com.hyejin.space_booking.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyejin.space_booking.repository.SpaceRepository;

/**
 * 공간 상세조회 응답값
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SpaceInfoResponse(
        Long spaceId,
        String  title, // 공간 이름
        String description, // 설명
        Integer capacity, // 수용인원
        String contact, // 연락처
        String  address, // 주소
        String  imageUrl, // 이미지 url
        String time, // 오픈~종료시간
        String  regDate, // 게시일자
        Integer minPriceNumeric, // 최소 시간당 금액
        String  minPrice // 최소 시간당 금액 (한글)
) {
    public static SpaceInfoResponse from(SpaceRepository.DetailRow r) {
        return new SpaceInfoResponse(
                r.getSpaceId(),
                r.getTitle(),
                r.getDescription(),
                r.getCapacity(),
                r.getContact(),
                r.getAddress(),
                r.getImageUrl(),
                r.getTime(),
                r.getRegDate(),
                r.getMinPriceNumeric() == null ? null : r.getMinPriceNumeric().intValue(),
                r.getMinPrice()
        );
    }

}
