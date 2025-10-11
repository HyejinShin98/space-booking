package com.hyejin.space_booking.api.request;

/* 카카오 로그인 연동 콜백 */
public record KakaoLoginCallbackRequest(

        String userId,
        String userPw
) {}