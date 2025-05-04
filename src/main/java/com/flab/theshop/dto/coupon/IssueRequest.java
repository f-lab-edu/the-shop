package com.flab.theshop.dto.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueRequest {

    private Long couponPolicyId;

    private String userId;
}
