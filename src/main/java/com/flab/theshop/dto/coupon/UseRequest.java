package com.flab.theshop.dto.coupon;

import lombok.Getter;

@Getter
public class UseRequest {

    private Long orderId;
    private String userId;
}
