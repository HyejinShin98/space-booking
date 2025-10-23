package com.hyejin.space_booking.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.entity.UserSns;

import java.time.LocalDateTime;

/**
 * 회원정보 조회
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserInfoResponse(
        Long userKey,
        String userId,
        String email,
        String name,
        String birthDate,
        String phoneNum,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime regDate,
        String remarks,

        // SNS 연동정보
        String snsYn,
        String provider,
        String providerUserId,
        String profileImageUrl
) {
    // 정적 팩토리 메서드로 변환 로직 통합
    public UserInfoResponse(User user, UserSns userSns) {
        this(
                user.getUserKey(),
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getPhoneNum(),
                user.getRegDate(),
                user.getRemarks(),
                userSns != null ? "Y" : "N",
                userSns != null ? userSns.getProvider() : null,
                userSns != null ? userSns.getProviderUserId() : null,
                userSns != null ? userSns.getProfileImageUrl() : null
        );
    }
}
