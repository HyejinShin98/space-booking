package com.hyejin.space_booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name="code")
@Getter @Setter @NoArgsConstructor
public class Code {
    @Id
    @Column(name="code")
    private String code; // 코드

    @Column(name="code_name", nullable = false)
    private String codeName; // 코드이름

    @Column(name="up_code", nullable = true)
    private String upCode; // 상위코드

    @Column(name="depth", nullable = false)
    private String depth; // depth

    @Column(name="etc", nullable = true)
    private String etc; // 기타

    @Column(name="use_yn", nullable = false, length=1)
    private String useYn; // 사용여부






}
