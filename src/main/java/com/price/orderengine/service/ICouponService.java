package com.price.orderengine.service;

import com.price.orderengine.entity.Coupon;

public interface ICouponService {
    Coupon validateCoupon(String code);
    Coupon reserveCoupon(String code);
}
