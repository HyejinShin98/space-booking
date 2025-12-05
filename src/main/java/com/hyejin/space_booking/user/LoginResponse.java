package com.hyejin.space_booking.user;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 로그인 성공 응답값
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserInfoResponse user
) {
    public static LoginResponse of(String accessToken,
                                   String refreshToken,
                                   String tokenType,
                                   long expiresIn,
                                   UserInfoResponse user) {
        return new LoginResponse(accessToken, refreshToken, tokenType, expiresIn, user);
    }
}