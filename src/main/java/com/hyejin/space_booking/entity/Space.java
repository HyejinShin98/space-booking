package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="space")
@Getter @Setter @NoArgsConstructor
public class Space {
    @Id
    @Column(name="space_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer spaceId;

    @Column(name="title", nullable = false)
    private String title; // 공간이름

    @Column(name="description", nullable = false)
    private String description; // 공간소개

    @Column(name="address", nullable = false)
    private String address; // 주소

    @Column(name="capacity", nullable = false)
    private int capacity; // 수용인원

    @Column(name="contact", nullable = false)
    private String contact; // 연락처

    @Column(name="image_url", nullable = false)
    private String imageUrl; // 이미지 url

    @Column(name="open_time", nullable = false)
    private String openTime; // 오픈시간 2자리

    @Column(name="close_time", nullable = false)
    private String closeTime; // 종료시간 2자리

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;

    @Column(name="upt_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime uptDate;

    @Column(name="use_yn", nullable = false, length=1)
    private String useYn;






}
