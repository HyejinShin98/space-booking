package com.hyejin.space_booking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_key", referencedColumnName = "user_key", nullable = false, unique = true)
    private User user;

    @Column(name = "provider", nullable = false)
    private String provider; // 소셜명 KAKAO, NAVER

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId; // 소셜아이디 key

    @Column(name = "nickname", nullable = false)
    private String nickname; // 소셜닉네임

    @Column(name = "profile_image_url", nullable = false)
    private String profileImageUrl; // 프로필이미지

    @Column(name = "access_token", length = 2048)
    private String accessToken;

    @Column(name = "refresh_token", length = 2048)
    private String refreshToken;

    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "reg_date", insertable = false, updatable = false)
    private LocalDateTime regDate;
}
