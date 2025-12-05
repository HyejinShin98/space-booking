package com.hyejin.space_booking.external.pg;

import com.hyejin.space_booking.payment.PaymentConfirmRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class FakePgClient implements PgClient {

    @Override
    public PgConfirmResult confirm(PaymentConfirmRequest request) {
        // 테스트용: PG에 진짜로 안 날리고, 무조건 성공했다고 치는 로직
        log.info("FakePgClient.confirm() 호출됨. orderId={}", request.orderId());

        return new PgConfirmResult(
                "PG-TEST-" + UUID.randomUUID(),
                request.amount(),          // PaymentConfirmRequest에 amount 있다고 가정
                LocalDateTime.now()
        );
    }
}
