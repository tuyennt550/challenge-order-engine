package com.price.orderengine.service;

import com.price.orderengine.entity.Coupon;
import com.price.orderengine.errors.DBNotFoundException;
import com.price.orderengine.errors.ErrorCode;
import com.price.orderengine.errors.UserFriendlyException;
import com.price.orderengine.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    public Coupon validateCoupon(String code) {

        Coupon coupon = couponRepository.findByCodeAndActiveTrue(code)
                .orElseThrow(() ->
                        new DBNotFoundException(ErrorCode.COUPON_NOT_FOUND,
                                "Coupon not found: " + code));

        if (!coupon.isValid()) {
            throw new UserFriendlyException(
                    ErrorCode.INVALID_COUPON,
                    "Invalid coupon: " + coupon.getCode());
        }

        return coupon;
    }

    @Transactional
    public Coupon reserveCoupon(String code) {

        int updated = couponRepository.redeemCoupon(code);

        if (updated == 0) {
            throw new UserFriendlyException(
                    ErrorCode.COUPON_EXHAUSTED,
                    "Coupon exhausted or invalid: " + code);
        }

        return couponRepository.findByCode(code).orElseThrow();
    }
}
