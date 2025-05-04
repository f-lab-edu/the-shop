package com.flab.theshop.service.v3;

import com.flab.theshop.dto.coupon.IssueMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponProducer {

    private static final String TOPIC = "coupon-issue-requests";
    private final KafkaTemplate<String, IssueMessage> kafkaTemplate;

    public void sendCouponIssueRequest(IssueMessage message) {
        kafkaTemplate.send(TOPIC, String.valueOf(message.getPolicyId()), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
                    } else {
                        log.error("메세지 전송 불가=[{}] / {}", message, ex.getMessage());
                    }
                });
    }
}
