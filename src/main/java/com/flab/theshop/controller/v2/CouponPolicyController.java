package com.flab.theshop.controller.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.flab.theshop.controller.Response;
import com.flab.theshop.domain.CouponPolicy;
import com.flab.theshop.dto.coupon.CouponPolicyResponse;
import com.flab.theshop.dto.coupon.CreateRequest;
import com.flab.theshop.service.v2.CouponPolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.flab.theshop.exception.SuccessMessage.COUPON_POLICY_CREATE_OK;
import static com.flab.theshop.exception.SuccessMessage.GET_COUPON_POLICY;

@RestController("couponPolicyControllerV2")
@RequestMapping("/api/v2/coupon-policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    @PostMapping
    public Response<CouponPolicyResponse> createCouponPolicy(@Valid @RequestBody CreateRequest request)  throws JsonProcessingException {
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
