package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원정보
 */
@Entity @Table(name="user")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id
    @Column(name="user_id")
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

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;

    @Column(name="upt_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime uptDate;

    @Column(name="use_yn", nullable = false, length=1)
    private String useYn;

    // SNS 연동정보
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserSns userSns;

}
