package com.hyejin.space_booking.global;

public record JwtPair(
        String accessToken,
        String refreshToken,
        long accessTokenTtlSeconds,
        long refreshTokenTtlSeconds
) {}
