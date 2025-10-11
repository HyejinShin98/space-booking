package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="reservation")
@Getter @Setter @NoArgsConstructor
public class Reservation {
    @Id
    @Column(name="resv_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer resvId;

    @Column(name="user_id", nullable = false)
    private String userId;

    @Column(name="space_id", nullable = false)
    private Integer spaceId;

    @Column(name="visit_date", nullable = false)
    private String visitDate; // 방문일자 8자리

    @Column(name="start_time", nullable = false)
    private String startTime; // 시작시간 2자리

    @Column(name="end_time", nullable = false)
    private String endTime; // 종료시간 2자리

    @Column(name="guest_count", nullable = false)
    private int guestCount; // 방문인원

    @Column(name="request_note", nullable = true)
    private String requestNote; // 요청사항

    @Column(name="resv_phone", nullable = false)
    private String resvPhone; // 예약자휴대폰 11자리

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;

    @Column(name="upt_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime uptDate;

    @Column(name="cancel_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime cancelDate; // 취소일자

    @Column(name="status", nullable = false)
    private String status; // 예약상태 (예약중:BOOK, 취소:CANCEL, 방문완료:COMPLETE)






}
