package com.hyejin.space_booking.reservation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="reservation")
@Getter @Setter
public class Reservation {
    @Id
    @Column(name="resv_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long resvId;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private ReservationStatus status;

    @Column(name="user_key", nullable = false)
    private Long userKey;

    @Column(name="space_id", nullable = false)
    private Long spaceId;

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

    @Column(name="amount", nullable = false)
    private int amount; // 결제금액

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;

    @Column(name="upt_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime uptDate;

    @Column(name="cancel_date", insertable=false, updatable=false, nullable = true)
    private LocalDateTime cancelDate; // 취소일자

    @Column(name="remarks", nullable = false)
    private String remarks; // 비고


    protected Reservation() {}

    public static Reservation createPending(ReservationCreateRequest req) {
        Reservation reservation = new Reservation();
        reservation.userKey = req.userKey();
        reservation.spaceId = req.spaceId();
        reservation.visitDate = req.visitDate();
        reservation.startTime = req.startTime();
        reservation.endTime = req.endTime();
        reservation.guestCount = req.guestCount();
        reservation.requestNote = req.requestNote();
        reservation.resvPhone = req.resvPhone();
        reservation.amount = req.amount();
        reservation.status = ReservationStatus.PENDING;
        return reservation;
    }

    /* 상태변경 */
    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }





}
