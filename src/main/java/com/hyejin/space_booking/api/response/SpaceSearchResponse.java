package com.hyejin.space_booking.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 공간 목록 조회 응답값
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SpaceSearchResponse(
        Long spaceId,
        String  title,
        String  address,
        String  imageUrl,
        Integer capacity,
        Integer minPriceNumeric,
        String  minPrice,
        String  regDate
) {}
