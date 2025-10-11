package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 회원 좋아요 목록 
 */
@Entity @Table(name="user_like")
@Getter @Setter @NoArgsConstructor
public class UserLike {
    @Id
    @Column(name="user_like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer userLikeId;

    @Column(name="user_id", nullable = false)
    private String userId;

    @Column(name="space_id", nullable = false)
    private String spaceId;

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;


}
