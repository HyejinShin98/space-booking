package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원 SNS 연동정보
 */
@Entity
@Table(
        name = "user_sns",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_sns_provider_uid", columnNames = {"provider", "provider_user_id"})
            ,@UniqueConstraint(name = "uk_user_sns_user_key", columnNames = {"user_key"}) // 1:1만 허용
        },
        indexes = {
            @Index(name = "ix_user_sns_user_key", columnList = "user_key")
        }
)
@Getter @Setter @NoArgsConstructor
public class UserSns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sns_id")
    private Long userSnsId;

    // FK는 이제 user_key로
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_key", referencedColumnName = "user_key", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 32)
    private String provider; // KAKAO, NAVER 등

    @Column(name = "provider_user_id", nullable = false, length = 128)
    private String providerUserId;

    @Column(name = "access_token", length = 2048)
    private String accessToken;

    @Column(name = "refresh_token", length = 2048)
    private String refreshToken;

    @Column(name = "reg_date", insertable = false, updatable = false)
    private LocalDateTime regDate;
}
