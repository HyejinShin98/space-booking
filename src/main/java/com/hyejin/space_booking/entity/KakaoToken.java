package com.hyejin.space_booking.entity;

public record KakaoToken(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long   expiresIn,
        String scope
) {}
