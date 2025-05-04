package com.flab.theshop.controller.v3;

import com.flab.theshop.controller.Response;
import com.flab.theshop.domain.Coupon;
import com.flab.theshop.dto.coupon.CancelRequest;
import com.flab.theshop.dto.coupon.CouponResponse;
import com.flab.theshop.dto.coupon.IssueRequest;
import com.flab.theshop.dto.coupon.UseRequest;
import com.flab.theshop.service.v3.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.flab.theshop.exception.SuccessMessage.COUPON_CANCEL_SUCCESS;
import static com.flab.theshop.exception.SuccessMessage.COUPON_USE_SUCCESS;

@RestController("couponControllerV3")
@RequiredArgsConstructor
@RequestMapping("/api/v3/coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/issue")
    public Response<Void> issueCoupon(@RequestBody IssueRequest request) {
        couponService.requestCouponIssue(request);
        return Response.success(COUPON_CANCEL_SUCCESS.getMessage());
    }

    @PostMapping("/{couponId}/use")
    public Response<CouponResponse> useCoupon(
            @PathVariable("couponId") Long couponId,
            @RequestBody UseRequest request
    ) {
        Coupon coupon = couponService.useCoupon(couponId, request);
        return Response.success(COUPON_USE_SUCCESS.getMessage(), CouponResponse.from(coupon));
    }

    @PostMapping("/{couponId}/cancel")
    public Response<Void> cancelCoupon(@PathVariable("couponId") Long couponId, @RequestBody CancelRequest request) {
        couponService.cancelCoupon(couponId, request);
        return Response.success(COUPON_CANCEL_SUCCESS.getMessage());
    }
}
