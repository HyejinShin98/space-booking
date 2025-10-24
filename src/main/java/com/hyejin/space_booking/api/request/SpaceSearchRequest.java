package com.hyejin.space_booking.api.request;

import com.hyejin.space_booking.entity.PaginatableRequest;

/* 공간 검색 항목 */
public record SpaceSearchRequest(
        String keyword,  // 제목/설명/주소 like
        String option,   // 검색옵션
        String dayOfWeek,// 요일 '1'~'7' 등 DB 값과 동일
        String time,     // "15" 또는 "09" (HH)
        Integer capacity,  // 수용인원
        Integer page,
        Integer size,
        String sort         // "price_asc"|"price_desc"|"recent"
) implements PaginatableRequest {}