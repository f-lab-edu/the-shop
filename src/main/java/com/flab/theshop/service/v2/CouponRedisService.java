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
            //Redisson 분산 락 획득(쿠폰 정책별로 발급)
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                throw CouponException.COUPON_ISSUE_RATE_LIMIT.get();
            }

            //쿠폰 정책 유효성 검증
            //redis에 캐싱된 정책 조회 후 없으면 db fallback
            CouponPolicy couponPolicy = couponPolicyService.getCouponPolicy(request.getCouponPolicyId());

            //기간 유효성 체크
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

            // 쿠폰 db에 저장
            // redis는 수량 제어만, 실제 쿠폰은 db에 저장됨
            return couponRepository.save(Coupon.builder()
                    .couponPolicy(couponPolicy)
                    .member(member)
                    .couponCode(generateCouponCode())
                    .build());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CouponException.COUPON_ISSUE_FAILED.get();
        } finally {
            // 락 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String generateCouponCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
