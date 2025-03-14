package com.flab.theshop.service;

import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.dto.coupon.CreateRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.respository.CouponPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    /**
     * 쿠폰 정책 생성
     */
    @Transactional
    public CouponPolicy createCouponPolicy(CreateRequest request) {
        CouponPolicy couponPolicy = request.toEntity();
        return couponPolicyRepository.save(couponPolicy);
    }

    /**
     * 쿠폰 정책 조회
     */
    public CouponPolicy getCouponPolicy(Long id) {
        return couponPolicyRepository.findById(id)
                .orElseThrow(CouponException.COUPON_POLICY_NOT_FOUND::get);
    }

    /**
     * 쿠폰 정책 전체 조회
     */
    public List<CouponPolicy> getAllCouponPolicies() {
        return couponPolicyRepository.findAll();
    }
}
