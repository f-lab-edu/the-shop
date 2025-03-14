package com.flab.theshop.exception.coupon;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum CouponException {

    COUPON_ALREADY_USED(CONFLICT, "이미 사용중인 쿠폰입니다."),
    COUPON_EXPIRED(GONE, "만료된 쿠폰입니다."),
    COUPON_NOT_USED(BAD_REQUEST, "아직 사용되지 않은 쿠폰입니다."),
    COUPON_NOT_FOUND(NOT_FOUND, "쿠폰을 찾을 수 없거나 접근 권한이 없습니다."),
    COUPON_POLICY_NOT_FOUND(NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다.")
    ;

    private final CouponTaskException couponTaskException;

    CouponException(HttpStatus status, String message) {
        this.couponTaskException = new CouponTaskException(status.value(), message);
    }

    public CouponTaskException get() {
        return couponTaskException;
    }
}
