package com.flab.theshop.service.v1;

import com.flab.theshop.domain.*;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.respository.CouponPolicyRepository;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import com.flab.theshop.respository.OrderRepository;
import com.flab.theshop.service.v1.CouponService;
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
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Member member;
    private CouponPolicy couponPolicy;
    private Order order;

    private static final String TEST_USER_ID = "testUser";

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .userId(TEST_USER_ID)
                .name("테스트 유저")
                .passwordHash("test123")
                .role(Role.BUYER)
                .address("서울시")
                .phoneNumber("010-1234-5678")
                .build();

        member = memberRepository.save(member);

        couponPolicy = CouponPolicy.builder()
                .name("테스트 쿠폰")
                .type(DiscountType.FIXED_AMOUNT)
                .discountValue(1000)
                .minimumOrderAmount(10000)
                .maximumDiscountAmount(1000)
                .totalQuantity(100)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build();

        couponPolicy = couponPolicyRepository.save(couponPolicy);

        order = Order.builder()
                .member(member)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.ORDER)
                .build();

        order = orderRepository.save(order);
    }

    @Test
    @DisplayName("쿠폰 발급 성공")
    void issueCoupon_success() {
        // Given
        IssueRequest request = IssueRequest.builder()
                .couponPolicyId(couponPolicy.getId())
                .userId(member.getUserId())
                .build();

        // When
        Coupon coupon = couponService.issueCoupon(request);

        // Then
        assertThat(coupon.getCouponPolicy().getId()).isEqualTo(couponPolicy.getId());
        assertThat(coupon.getMember().getId()).isEqualTo(member.getId());
        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.AVAILABLE);
    }

    @Test
    @DisplayName("쿠폰 동시 발급 테스트")
    void issueCouponConcurrency() throws InterruptedException {
        // Given
        int totalQuantity = 100;
        couponPolicy = CouponPolicy.builder()
                .name("동시성 테스트 쿠폰")
                .type(DiscountType.FIXED_AMOUNT)
                .discountValue(1000)
                .minimumOrderAmount(10000)
                .maximumDiscountAmount(1000)
                .totalQuantity(totalQuantity)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build();
        couponPolicy = couponPolicyRepository.save(couponPolicy);

        int threadCount = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    // 유저 고유하게 생성
                    Member threadMember = Member.builder()
                            .userId("user_" + finalI)
                            .name("유저" + finalI)
                            .passwordHash("pw")
                            .role(Role.BUYER)
                            .phoneNumber("010-0000-000" + finalI)
                            .address("서울")
                            .build();
                    memberRepository.save(threadMember);

                    IssueRequest request = IssueRequest.builder()
                            .couponPolicyId(couponPolicy.getId())
                            .userId(threadMember.getUserId())
                            .build();

                    try {
                        couponService.issueCoupon(request);
                    } catch (Exception e) {
                        // 수량 초과 예외 발생 가능, 무시
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드 완료 대기

        // 쿠폰이 100개만 발급되었는지 검증
        long issuedCount = couponRepository.count();
        assertThat(issuedCount).isEqualTo(totalQuantity);
    }

    @Test
    @DisplayName("쿠폰 사용 성공")
    void useCoupon_success() {
        // 발급
        Coupon issued = couponRepository.save(
                Coupon.builder()
                        .member(member)
                        .couponPolicy(couponPolicy)
                        .couponCode("TEST123")
                        .build());

        UseRequest request = UseRequest.builder()
                .userId(member.getUserId())
                .orderId(order.getId())
                .build();

        // When
        Coupon used = couponService.useCoupon(issued.getId(), request);

        // Then
        assertThat(used.getOrder().getId()).isEqualTo(order.getId());
        assertThat(used.getStatus()).isEqualTo(CouponStatus.USED);
    }

    @Test
    @DisplayName("쿠폰 취소 성공")
    void cancelCoupon_success() {
        Coupon coupon = Coupon.builder()
                .member(member)
                .couponPolicy(couponPolicy)
                .couponCode("TEST123")
                .build();

        Coupon issuedCoupon = couponRepository.save(coupon);
        issuedCoupon.use(order); // 사용 상태로 만듦
        couponRepository.save(issuedCoupon);

        CancelRequest request = CancelRequest.builder()
                .userId(member.getUserId())
                .build();

        // When
        Coupon canceled = couponService.cancelCoupon(issuedCoupon.getId(), request);

        // Then
        assertThat(canceled.getStatus()).isEqualTo(CouponStatus.CANCELED);
        assertThat(canceled.getOrder()).isNull();
    }
}
