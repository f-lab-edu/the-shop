package com.flab.theshop.exception.coupon;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum CouponException {

    COUPON_ALREADY_USED(CONFLICT, "이미 사용중인 쿠폰입니다."),
    COUPON_EXPIRED(GONE, "만료된 쿠폰입니다."),
    COUPON_NOT_USED(BAD_REQUEST, "아직 사용되지 않은 쿠폰입니다."),
    COUPON_NOT_FOUND(NOT_FOUND, "쿠폰을 찾을 수 없거나 접근 권한이 없습니다."),
    COUPON_POLICY_NOT_FOUND(NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다."),
    COUPON_PERIOD_INVALID(BAD_REQUEST, "쿠폰 발급 기간이 아닙니다."),
    COUPON_OUT_OF_STOCK(BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),
    COUPON_ISSUE_FAILED(BAD_REQUEST, "쿠폰 발급 중 오류가 발생했습니다."),
    COUPON_ISSUE_RATE_LIMIT(TOO_MANY_REQUESTS, "쿠폰 발급 요청이 많아 처리할 수 없습니다. 잠시 후 다시 시도해주세요."),
    ;

    private final CouponTaskException couponTaskException;

    CouponException(HttpStatus status, String message) {
        this.couponTaskException = new CouponTaskException(status.value(), message);
    }

    public CouponTaskException get() {
        return couponTaskException;
    }
}
