package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="reservation_detail")
@Getter @Setter @NoArgsConstructor
public class ReservationPayment {
    @Id
    @Column(name="resv_pay_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer resvPayId;

    @Column(name="resv_id", nullable = false)
    private Integer resvId; // 예약 id

    @Column(name="total", nullable = false)
    private int total; // 결제금액

    @Column(name="pay_gubn", nullable = false)
    private String payGubn; // 결제방식

    @Column(name="status", nullable = false)
    private String status; // 결제상태 (결제대기:PD / 결제완료:PC / 결제취소:CN)

    @Column(name="pay_no", nullable = true)
    private String payNo; // pg사 결제번호

    @Column(name="pay_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime payDate; // 결제일자


}
