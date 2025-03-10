package com.flab.theshop.domain;

import com.flab.theshop.exception.coupon.CouponException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.flab.theshop.domain.CouponStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id @GeneratedValue
    @Column(name = "coupon_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_policy_id")
    private CouponPolicy couponPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime usedAt;

    /**
     * 쿠폰 사용
     */
    public void use(Order order) {
        if (isUsed()) {
            throw CouponException.COUPON_ALREADY_USED.get();
        }

        if (isExpired()) {
            throw CouponException.COUPON_EXPIRED.get();
        }

        this.status = USED;
        this.order = order;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * 쿠폰 사용 취소
     */
    public void cancel() {
        if (!isUsed()) {
            throw CouponException.COUPON_NOT_USED.get();
        }

        this.status = CANCELED;
        this.order = null;
        this.usedAt = null;
    }

    private boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(couponPolicy.getStartTime()) || now.isAfter(couponPolicy.getEndTime());
    }

    private boolean isUsed() {
        return status == USED;
    }
}
