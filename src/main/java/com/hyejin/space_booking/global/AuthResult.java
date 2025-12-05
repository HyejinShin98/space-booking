package com.hyejin.space_booking.global;

public record AuthResult(
        Long userId,
        String accessJwt,
        long accessTtlSeconds
) {}
