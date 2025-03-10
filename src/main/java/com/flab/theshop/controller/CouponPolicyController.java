package com.flab.theshop.controller;

import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.dto.coupon.CouponPolicyResponse;
import com.flab.theshop.dto.coupon.CreateRequest;
import com.flab.theshop.service.CouponPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.flab.theshop.exception.SuccessMessage.COUPON_POLICY_CREATE_OK;
import static com.flab.theshop.exception.SuccessMessage.GET_COUPON_POLICY;

@RestController
@RequestMapping("/api/coupon-policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @PostMapping
    public Response<CouponPolicyResponse> createCouponPolicy(@Valid @RequestBody CreateRequest request) {
        CouponPolicy couponPolicy = couponPolicyService.createCouponPolicy(request);
        return Response.success(COUPON_POLICY_CREATE_OK.getMessage(), CouponPolicyResponse.from(couponPolicy));
    }

    @GetMapping("/{id}")
    public Response<CouponPolicyResponse> getCouponPolicy(@PathVariable Long id) {
        CouponPolicy couponPolicy = couponPolicyService.getCouponPolicy(id);
        return Response.success(GET_COUPON_POLICY.getMessage(), CouponPolicyResponse.from(couponPolicy));
    }

    @GetMapping
    public Response<List<CouponPolicyResponse>> getAllCouponPolicies() {
        List<CouponPolicyResponse> couponPolicies = couponPolicyService.getAllCouponPolicies().stream()
                .map(CouponPolicyResponse::from)
                .collect(Collectors.toList());
        return Response.success(GET_COUPON_POLICY.getMessage(), couponPolicies);
    }
}
