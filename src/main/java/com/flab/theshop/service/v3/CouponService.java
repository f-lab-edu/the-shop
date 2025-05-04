package com.flab.theshop.service.v3;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.Member;
import com.flab.theshop.domain.Order;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.IssueMessage;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.exception.item.ItemException;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import com.flab.theshop.respository.OrderRepository;
import com.flab.theshop.service.v2.CouponPolicyService;
import com.flab.theshop.service.v2.CouponStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("couponServiceV3")
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_LOCK_KEY = "coupon:lock:";
    private static final long LOCK_WAIT_TIME = 3L;
    private static final long LOCK_LEASE_TIME = 5L;

    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final CouponProducer couponProducer;
    private final CouponStateService couponStateService;
    private final CouponPolicyService couponPolicyService;

    @Transactional(readOnly = true)
    public void requestCouponIssue(IssueRequest request) {
        String quantityKey = COUPON_QUANTITY_KEY + request.getCouponPolicyId();
        String lockKey = COUPON_LOCK_KEY + request.getCouponPolicyId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                throw CouponException.COUPON_ISSUE_RATE_LIMIT.get();
            }

            CouponPolicy couponPolicy = couponPolicyService.getCouponPolicy(request.getCouponPolicyId());
            if (couponPolicy == null) {
                throw CouponException.COUPON_POLICY_NOT_FOUND.get();
            }

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(couponPolicy.getStartTime()) || now.isAfter(couponPolicy.getEndTime())) {
                throw CouponException.COUPON_PERIOD_INVALID.get();
            }

            // 수량 체크 및 감소
            RAtomicLong atomicQuantity = redissonClient.getAtomicLong(quantityKey);
            long remainingQuantity = atomicQuantity.decrementAndGet();

            if (remainingQuantity < 0) {
                atomicQuantity.incrementAndGet();
                throw CouponException.COUPON_OUT_OF_STOCK.get();
            }

            // 현재 유저 ID 가져오고, Member 조회
            String currentUserId = request.getUserId();
            Member member = memberRepository.findByUserId(currentUserId)
                    .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

            // Kafka로 쿠폰 발급 요청 전송
            couponProducer.sendCouponIssueRequest(
                    IssueMessage.builder()
                            .policyId(request.getCouponPolicyId())
                            .userId(member.getUserId())
                            .build()
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CouponException.COUPON_ISSUE_FAILED.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void issueCoupon(IssueMessage message) {
        try {
            CouponPolicy policy = couponPolicyService.getCouponPolicy(message.getPolicyId());
            if (policy == null) {
                throw CouponException.COUPON_POLICY_NOT_FOUND.get();
            }

            Member member = memberRepository.findByUserId(message.getUserId())
                    .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

            Coupon coupon = couponRepository.save(Coupon.builder()
                    .couponPolicy(policy)
                    .member(member)
                    .couponCode(generateCouponCode())
                    .build());

            log.info("쿠폰 발급 성공: policyId={}, userId={}", message.getPolicyId(), message.getUserId());

        } catch (Exception e) {
            log.error("쿠폰 발급 실패: {}", e.getMessage());
            throw e;
        }
    }

    public Coupon useCoupon(Long couponId, UseRequest request) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(ItemException.ORDER_NOT_FOUND::get);

        coupon.use(order);
        couponStateService.updateCouponState(coupon);

        return coupon;
    }

    public Coupon cancelCoupon(Long couponId, CancelRequest request) {
        // 현재 유저 ID 가져오고, Member 조회
        String currentUserId = request.getUserId();
        Member member = memberRepository.findByUserId(currentUserId)
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

        Coupon coupon = couponRepository.findByIdAndUserId(couponId, member.getUserId())
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);

        /*
        if (!coupon.isUsed()) {
            throw new IllegalStateException("사용되지 않은 쿠폰은 취소할 수 없습니다.");
        }
         */

        coupon.cancel();
        couponStateService.updateCouponState(coupon);

        return coupon;
    }

    private String generateCouponCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
