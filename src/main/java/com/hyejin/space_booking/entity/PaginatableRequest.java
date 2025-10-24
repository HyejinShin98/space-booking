package com.hyejin.space_booking.entity;

public interface PaginatableRequest {
    Integer page();   // 0-base
    Integer size();   // page size
    String  sort();   // 사용자 정의 소트 문자열 (ex. "price_asc")
}
