package com.flab.theshop.service.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.DiscountType;
import com.flab.theshop.domain.Member;
import com.flab.theshop.domain.Role;
import com.flab.theshop.dto.coupon.CreateRequest;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CouponRedisServiceTest {

    @Autowired
    private CouponRedisService couponRedisService;

    @Autowired
    private CouponPolicyService couponPolicyService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int TOTAL_QUANTITY = 100;

    private Long couponPolicyId;

    @BeforeEach
    void setUp() throws Exception {
        // 쿠폰 정책 저장
        CreateRequest policyRequest = CreateRequest.builder()
                .name("Redis 테스트 쿠폰")
                .discountValue(1000)
                .discountType(DiscountType.FIXED_AMOUNT)
                .maximumDiscountAmount(1000)
                .minimumOrderAmount(10000)
                .totalQuantity(TOTAL_QUANTITY)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .build();

        CouponPolicy savedPolicy = couponPolicyService.createCouponPolicy(policyRequest);
        couponPolicyId = savedPolicy.getId();
    }

    @Test
    @DisplayName("200명 동시 요청 시 쿠폰은 정확히 100개만 발급된다")
    void issueCoupon_concurrent() throws InterruptedException {
        int threadCount = 200;
        ExecutorService executor = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int userNum = i;
            executor.execute(() -> {
                try {
                    // 유저 생성
                    Member member = Member.builder()
                            .userId("user_" + userNum)
                            .name("유저" + userNum)
                            .passwordHash("pw")
                            .role(Role.BUYER)
                            .address("서울")
                            .phoneNumber("010-0000-000" + userNum)
                            .build();
                    memberRepository.save(member);

                    // 발급 요청
                    IssueRequest request = IssueRequest.builder()
                            .couponPolicyId(couponPolicyId)
                            .userId(member.getUserId())
                            .build();

                    couponRedisService.issueCoupon(request);
                } catch (Exception e) {
                    // 수량 초과로 인한 실패는 무시
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 종료 대기

        // Then
        long count = couponRepository.count();
        assertThat(count).isEqualTo(TOTAL_QUANTITY);
    }
}
