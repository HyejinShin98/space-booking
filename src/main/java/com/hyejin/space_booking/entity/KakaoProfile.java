package com.hyejin.space_booking.entity;

import java.time.Instant;
import java.util.Set;

public record KakaoProfile(
        String providerUserId,   // kakao "id"를 문자열로
        String nickname,
        String profileImageUrl,
        String email,
        boolean emailVerified
) {}