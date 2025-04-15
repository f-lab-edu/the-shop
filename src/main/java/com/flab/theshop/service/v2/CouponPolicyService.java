package com.flab.theshop.service.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.dto.coupon.CouponPolicyResponse;
import com.flab.theshop.dto.coupon.CreateRequest;
import com.flab.theshop.exception.coupon.CouponException;
import com.flab.theshop.respository.CouponPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_POLICY_KEY = "coupon:policy:";

    /**
     * 쿠폰 정책 생성
     */
    @Transactional
    public CouponPolicy createCouponPolicy(CreateRequest request) throws JsonProcessingException {
        CouponPolicy couponPolicy = request.toEntity();
        CouponPolicy savedPolicy = couponPolicyRepository.save(couponPolicy);

        // Redis에 초기 수량 설정
        String quantityKey = COUPON_QUANTITY_KEY + savedPolicy.getId();
        RAtomicLong atomicQuantity = redissonClient.getAtomicLong(quantityKey);
        atomicQuantity.set(savedPolicy.getTotalQuantity());

        // Redis에 정책 정보 저장
        String policyKey = COUPON_POLICY_KEY + savedPolicy.getId();
        String policyJson = objectMapper.writeValueAsString(CouponPolicyResponse.from(savedPolicy));
        RBucket<String> bucket = redissonClient.getBucket(policyKey);
        bucket.set(policyJson);

        return savedPolicy;
    }

    public CouponPolicy getCouponPolicy(Long id) {
        String policyKey = COUPON_POLICY_KEY + id;
        RBucket<String> bucket = redissonClient.getBucket(policyKey);
        String policyJson = bucket.get();
        if (policyJson != null) {
            try {
                return objectMapper.readValue(policyJson, CouponPolicy.class);
            } catch (JsonProcessingException e) {
                log.error("쿠폰 정책 정보를 JSON으로 파싱하는 중 오류가 발생했습니다.", e);
            }
        }

        return couponPolicyRepository.findById(id)
                .orElseThrow(CouponException.COUPON_POLICY_NOT_FOUND::get);
    }

    public List<CouponPolicy> getAllCouponPolicies() {
        return couponPolicyRepository.findAll();
    }
}
