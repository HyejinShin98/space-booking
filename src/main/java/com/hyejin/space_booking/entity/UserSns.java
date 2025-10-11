package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원 SNS 연동정보
 */
@Entity @Table(name="user_sns")
@Getter @Setter @NoArgsConstructor
public class UserSns {
    @Id
    @Column(name="user_sns_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer userSnsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name="sns_gubn", nullable = false)
    private String snsGubn; // 네이버:NAVER / 카카오:KAKAO

    @Column(name="sns_key", nullable = true)
    private String snsKey;

    @Column(name="access_token", nullable = true)
    private String accessToken;

    @Column(name="refresh_token", nullable = true)
    private String refreshToken;

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;


}
