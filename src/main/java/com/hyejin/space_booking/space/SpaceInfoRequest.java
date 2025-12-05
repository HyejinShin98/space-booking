package com.hyejin.space_booking.space;

import jakarta.validation.constraints.NotNull;

/* 공간 상세조회 항목 */
public record SpaceInfoRequest(
        @NotNull Long spaceId
) {}