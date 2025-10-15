package com.hyejin.space_booking.entity;

public record AuthResult(
        Long userId,
        String accessJwt,
        long accessTtlSeconds
) {}
