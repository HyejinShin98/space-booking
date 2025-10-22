package com.hyejin.space_booking.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

/**
 * 회원정보
 */
@Entity @Table(name="user")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key")
    private Long userKey;

    // 로그인 아이디
    // 소셜연동 - 소셜+소셜아이디 -> ex) KAKAO12345
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name="user_pw", nullable = false)
    private String userPw;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="name", nullable = false)
    private String name; // 이름

    @Column(name="birth_date", nullable = false)
    private String birthDate; // 생년월일 8자리

    @Column(name="phone_num", nullable = false)
    private String phoneNum; // 휴대폰번호


    @Generated(GenerationTime.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;

    @Generated(GenerationTime.ALWAYS)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="upt_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime uptDate;

    @Column(name = "first_login_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime firstLoginDate; // 최초 로그인일자

    @Column(name = "last_login_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginDate; // 최종 로그인일자

    @Column(name="use_yn", nullable = false, length=1)
    private String useYn;

    @Column(name="remarks", nullable = true)
    private String remarks;

    // SNS 연동정보
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserSns userSns;

}
