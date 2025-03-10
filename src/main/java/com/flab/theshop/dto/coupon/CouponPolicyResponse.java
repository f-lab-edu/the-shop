package com.flab.theshop.dto.coupon;

import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.domain.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponPolicyResponse {

    private Long id;
    private String name;
    private String description;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer minimumOrderAmount;
    private Integer maximumDiscountAmount;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static CouponPolicyResponse from(CouponPolicy couponPolicy) {
        return CouponPolicyResponse.builder()
                .id(couponPolicy.getId())
                .name(couponPolicy.getName())
                .description(couponPolicy.getDescription())
                .discountType(couponPolicy.getType())
                .discountValue(couponPolicy.getDiscountValue())
                .minimumOrderAmount(couponPolicy.getMinimumOrderAmount())
                .maximumDiscountAmount(couponPolicy.getMaximumDiscountAmount())
                .totalQuantity(couponPolicy.getTotalQuantity())
                .startTime(couponPolicy.getStartTime())
                .endTime(couponPolicy.getEndTime())
                .build();
    }
}
