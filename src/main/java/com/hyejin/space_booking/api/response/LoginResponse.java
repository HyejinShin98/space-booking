package com.hyejin.space_booking.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.entity.UserSns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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