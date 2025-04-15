package com.flab.theshop.service.v2;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.Member;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRedisService {

    private final RedissonClient redissonClient;
    private final CouponRepository couponRepository;
    private final CouponPolicyService couponPolicyService;
    private final MemberRepository memberRepository;

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_LOCK_KEY = "coupon:lock:";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 5;

    @Transactional
//    @CouponMetered(version = "v2")
    public Coupon issueCoupon(IssueRequest request) {
        String quantityKey = COUPON_QUANTITY_KEY + request.getCouponPolicyId();
        String lockKey = COUPON_LOCK_KEY + request.getCouponPolicyId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                throw CouponException.COUPON_ISSUE_RATE_LIMIT.get();
            }

            CouponPolicy couponPolicy = couponPolicyService.getCouponPolicy(request.getCouponPolicyId());

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

            // 쿠폰 발급
            return couponRepository.save(Coupon.builder()
                    .couponPolicy(couponPolicy)
                    .member(member)
                    .couponCode(generateCouponCode())
                    .build());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CouponException.COUPON_ISSUE_FAILED.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String generateCouponCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
