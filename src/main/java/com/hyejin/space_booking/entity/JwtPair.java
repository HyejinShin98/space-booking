package com.hyejin.space_booking.entity;

public record JwtPair(
        String accessToken,
        String refreshToken,
        long accessTokenTtlSeconds,
        long refreshTokenTtlSeconds
) {}
