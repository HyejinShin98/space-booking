package com.hyejin.space_booking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="space_slot")
@Getter @Setter @NoArgsConstructor
public class SpaceSlot {
    @Id
    @Column(name="space_slot_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Integer spaceSlotId;

    @Column(name="space_id", nullable = false)
    private String spaceId; // 공간 id

    @Column(name="day_of_week", nullable = false)
    private String dayOfWeek; // 요일(1:월.. 7:일)

    @Column(name="start_time", nullable = false)
    private String startTime; // 시작시간 2자리

    @Column(name="end_time", nullable = false)
    private String endTime; // 종료시간 2자리

    @Column(name="price", nullable = false)
    private int price; // 금액

}
