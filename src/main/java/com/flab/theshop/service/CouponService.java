package com.flab.theshop.service;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.Member;
import com.flab.theshop.domain.Order;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.exception.item.ItemException;
import com.flab.theshop.exception.member.MemberException;
import com.flab.theshop.respository.CouponRepository;
import com.flab.theshop.respository.MemberRepository;
import com.flab.theshop.respository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

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
        return couponRepository.findByIdAndUserId(couponId, userId)
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
