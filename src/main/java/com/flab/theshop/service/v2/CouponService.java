package com.flab.theshop.service.v2;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.Order;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.CouponResponse;
import com.flab.theshop.dto.coupon.IssueRequest;
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
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final CouponRedisService couponRedisService;
    private final CouponStateService couponStateService;

    @Transactional
    public CouponResponse issueCoupon(IssueRequest request) {
        Coupon coupon = couponRedisService.issueCoupon(request);
        couponStateService.updateCouponState(couponRepository.findById(coupon.getId())
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get));

        return CouponResponse.from(coupon);
    }

    @Transactional
    public Coupon useCoupon(Long couponId, UseRequest request) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(ItemException.ORDER_NOT_FOUND::get);

        coupon.use(order);
        couponStateService.updateCouponState(coupon);

        return coupon;
    }

    @Transactional
    public void cancelCoupon(Long couponId, CancelRequest request) {
        memberRepository.findByUserId(request.getUserId())
                .orElseThrow(MemberException.MEMBER_NOT_EXISTS::get);

        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);

        coupon.cancel();
        couponStateService.updateCouponState(coupon);
    }

    public CouponResponse getCoupon(Long couponId) {
        CouponResponse cachedCoupon = couponStateService.getCouponState(couponId);
        if (cachedCoupon != null) {
            return cachedCoupon;
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponException.COUPON_NOT_FOUND::get);

        CouponResponse response = CouponResponse.from(coupon);
        couponStateService.updateCouponState(coupon);

        return response;
    }
}
