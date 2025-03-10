package com.flab.theshop.dto.coupon;

import com.flab.theshop.domain.Coupon;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.CouponStatus;
import com.flab.theshop.domain.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponResponse {

    private Long id;
    private String userId;
    private String couponCode;
    private DiscountType discountType;
    private int discountValue;
    private int minimumOrderAmount;
    private int maximumDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private CouponStatus status;
    private Long orderId;
    private LocalDateTime usedAt;

    public static CouponResponse from(Coupon coupon) {
        CouponPolicy policy = coupon.getCouponPolicy();
        return CouponResponse.builder()
                .id(coupon.getId())
                .userId(coupon.getMember().getUserId())
                .discountType(policy.getType())
                .discountValue(policy.getDiscountValue())
                .minimumOrderAmount(policy.getMinimumOrderAmount())
                .maximumDiscountAmount(policy.getMaximumDiscountAmount())
                .validFrom(policy.getStartTime())
                .validUntil(policy.getEndTime())
                .status(coupon.getStatus())
                .orderId(coupon.getOrder().getId())
                .usedAt(coupon.getUsedAt())
                .build();
    }
}
