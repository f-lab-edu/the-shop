package com.flab.theshop.dto.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IssueMessage {

    private Long policyId;
    private String userId;
}
