package com.flab.theshop.exception.coupon;

import lombok.Getter;

@Getter
public class CouponTaskException extends RuntimeException {

    private final int code;
    private final String message;

    public CouponTaskException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
