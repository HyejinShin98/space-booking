package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="reservation_detail")
@Getter @Setter @NoArgsConstructor
public class ReservationDetail {
    @Id
    @Column(name="resv_dtl_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer resvDtlId;

    @Column(name="resv_id", nullable = false)
    private Integer resvId; // 예약 id

    @Column(name="slot_id", nullable = false)
    private Integer slotId; // 금액대별정보 id

    @Column(name="price", nullable = false)
    private int price; // 금액

    @Column(name="reg_date", insertable=false, updatable=false, nullable = false)
    private LocalDateTime regDate;


}
