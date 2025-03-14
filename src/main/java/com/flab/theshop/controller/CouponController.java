package com.flab.theshop.controller;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.CouponResponse;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.flab.theshop.exception.SuccessMessage.COUPON_CANCEL_SUCCESS;
import static com.flab.theshop.exception.SuccessMessage.COUPON_USE_SUCCESS;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/{couponId}/use")
    public Response<CouponResponse> useCoupon(@PathVariable("couponId") Long couponId, @RequestBody UseRequest request) {
        Coupon coupon = couponService.useCoupon(couponId, request);
        return Response.success(COUPON_USE_SUCCESS.getMessage(), CouponResponse.from(coupon));
    }

    @PostMapping("/{couponId}/cancel")
    public Response<Void> cancelCoupon(@PathVariable("couponId") Long couponId, @RequestBody CancelRequest request) {
        Coupon coupon = couponService.cancelCoupon(couponId, request);
        return Response.success(COUPON_CANCEL_SUCCESS.getMessage());
    }
}
