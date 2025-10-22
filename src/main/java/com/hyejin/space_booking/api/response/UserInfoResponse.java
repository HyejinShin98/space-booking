package com.hyejin.space_booking.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyejin.space_booking.entity.User;
import com.hyejin.space_booking.entity.UserSns;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원정보 조회
 *
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
    private Long userKey;       // 회원 key
    private String userId;      // 회원 아이디
//    private String userPw;      // 비밀번호(암호화)
    private String email;       // 이메일
    private String name;        // 이름
    private String birthDate;   // 생년월일 yyyy-mm-dd
    private String phoneNum;    // 휴대폰번호 010-0000-0000
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;     // 가입일자 yyyy-mm-dd
    private String remarks;     // 비고

    // SNS 연동정보
    private String snsYn;       // 소셜연동여부 Y,N
    private String provider;    // 소셜명 KAKAO, NAVER
    private String providerUserId;  // 소셜아이디 key
    private String profileImageUrl; // 프로필 이미지 URL

    // 복합 생성자
    public UserInfoResponse(User user, UserSns userSns) {
        this.userKey = user.getUserKey();
        this.userId = user.getUserId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNum = user.getPhoneNum();
        this.birthDate = user.getBirthDate();
        this.regDate = user.getRegDate();
        this.remarks = user.getRemarks();

        // UserSns가 존재할 때만 값 세팅
        if (userSns != null) {
            this.snsYn = "Y";
            this.provider = userSns.getProvider();
            this.providerUserId = userSns.getProviderUserId();
            this.profileImageUrl = userSns.getProfileImageUrl();
        } else {
            this.snsYn = "N";
        }
    }

}
