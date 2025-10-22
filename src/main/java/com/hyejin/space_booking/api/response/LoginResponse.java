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
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private UserInfoResponse user;

}
