package com.flab.theshop.service.v1;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.Member;
import com.flab.theshop.domain.Order;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.exception.coupon.CouponTaskException;
import com.flab.theshop.exception.item.ItemException;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.CouponPolicyRepository;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import com.flab.theshop.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;
    private final OrderRepository orderRepository;

    private String generateCouponCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 1. Race Condition 발생 가능성
     * findByIdWithLock으로 쿠폰 정책에 대해 락을 걸지만, countByCouponPolicyId와 실제 쿠폰 저장 사이에 갭이 존재
     * 여러 트랜잭션이 동시에 카운트를 조회하고 조건을 통과한 후 쿠폰을 저장할 수 있음
     * 결과적으로 totalQuantity보다 더 많은 쿠폰이 발급될 수 있음
     *
     * 2. 성능 이슈
     * 매 요청마다 발급된 쿠폰 수를 카운트하는 쿼리 실행
     * 쿠폰 수가 많아질수록 카운트 쿼리의 성능이 저하될 수 있음
     * PESSIMISTIC_LOCK으로 인한 병목 현상 발생 가능
     *
     * 3. Dead Lock 가능성
     * 여러 트랜잭션이 동시에 같은 쿠폰 정책에 대해 락을 획득하려 할 때
     * 트랜잭션 타임아웃이 발생할 수 있음
     *
     * 4. 정확한 수량 보장의 어려움
     * 분산 환경에서 여러 서버가 동시에 쿠폰을 발급할 경우
     * DB 레벨의 락만으로는 정확한 수량 제어가 어려움
     */
    @Transactional
    public Coupon issueCoupon(IssueRequest request) {
        CouponPolicy couponPolicy = couponPolicyRepository.findByIdWithLock(request.getCouponPolicyId())
                .orElseThrow(CouponException.COUPON_POLICY_NOT_FOUND::get);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(couponPolicy.getStartTime()) || now.isAfter(couponPolicy.getEndTime())) {
            throw CouponException.COUPON_PERIOD_INVALID.get();
        }

        long issuedCouponCount = couponRepository.countByCouponPolicyId(couponPolicy.getId());
        if (issuedCouponCount >= couponPolicy.getTotalQuantity()) {
            throw CouponException.COUPON_OUT_OF_STOCK.get();
        }

        // 현재 유저 ID 가져오고, Member 조회
        String currentUserId = request.getUserId();
        Member member = memberRepository.findByUserId(currentUserId)
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

        Coupon coupon = Coupon.builder()
                .couponPolicy(couponPolicy)
                .member(member)
                .couponCode(generateCouponCode())
                .build();

        return couponRepository.save(coupon);
    }


    /**
     * 쿠폰 사용
     */
    @Transactional
    public Coupon useCoupon(Long couponId, UseRequest request) {
        Member member = findMember(request.getUserId());
        Coupon coupon = findCoupon(couponId, member.getUserId());
        Order order = findOrder(request.getOrderId());

        //쿠폰 사용
        coupon.use(order);

        return coupon;
    }

    /**
     * 쿠폰 사용 취소
     */
    @Transactional
    public Coupon cancelCoupon(Long couponId, CancelRequest request) {
        Member member = findMember(request.getUserId());
        Coupon coupon = findCoupon(couponId, member.getUserId());

        //쿠폰 사용 취소
        coupon.cancel();

        return coupon;
    }

    /**
     * 회원 조회
     */
    private Member findMember(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);
    }

    /**
     * 쿠폰 조회
     */
    private Coupon findCoupon(Long couponId, String userId) {
        return couponRepository.findByIdAndMember_UserId(couponId, userId)
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);
    }

    /**
     * 주문조회
     */
    private Order findOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(ItemException.ORDER_NOT_FOUND::get);
    }
}
