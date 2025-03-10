package com.flab.theshop.respository;

import com.flab.theshop.domain.CouponPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
}
