package com.price.orderengine.service;

import com.price.orderengine.entity.Coupon;
import com.price.orderengine.errors.DBNotFoundException;
import com.price.orderengine.errors.ErrorCode;
import com.price.orderengine.errors.UserFriendlyException;
import com.price.orderengine.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void validateCoupon_should_return_coupon_when_valid() {
        Coupon coupon = mock(Coupon.class);

        when(couponRepository.findByCodeAndActiveTrue("SUMMER10"))
                .thenReturn(Optional.of(coupon));

        when(coupon.isValid()).thenReturn(true);

        Coupon result = couponService.validateCoupon("SUMMER10");

        assertNotNull(result);
        assertEquals(coupon, result);

        verify(couponRepository).findByCodeAndActiveTrue("SUMMER10");
    }

    @Test
    void validateCoupon_should_throw_DBNotFoundException_when_not_found() {

        when(couponRepository.findByCodeAndActiveTrue("SUMMER10"))
                .thenReturn(Optional.empty());

        DBNotFoundException ex = assertThrows(
                DBNotFoundException.class,
                () -> couponService.validateCoupon("SUMMER10")
        );

        assertTrue(ex.getMessage().contains("SUMMER10"));

        verify(couponRepository).findByCodeAndActiveTrue("SUMMER10");
    }

    @Test
    void validateCoupon_should_throw_UserFriendlyException_when_invalid() {

        Coupon coupon = mock(Coupon.class);

        when(couponRepository.findByCodeAndActiveTrue("SUMMER10"))
                .thenReturn(Optional.of(coupon));

        when(coupon.isValid()).thenReturn(false);
        when(coupon.getCode()).thenReturn("SUMMER10");

        UserFriendlyException ex = assertThrows(
                UserFriendlyException.class,
                () -> couponService.validateCoupon("SUMMER10")
        );

        assertEquals(ErrorCode.INVALID_COUPON, ex.getErrorCode());

        verify(couponRepository).findByCodeAndActiveTrue("SUMMER10");
    }

    @Test
    void reserveCoupon_should_return_coupon_when_success() {

        Coupon coupon = mock(Coupon.class);

        when(couponRepository.redeemCoupon("SUMMER10"))
                .thenReturn(1);

        when(couponRepository.findByCode("SUMMER10"))
                .thenReturn(Optional.of(coupon));

        Coupon result = couponService.reserveCoupon("SUMMER10");

        assertNotNull(result);
        assertEquals(coupon, result);

        verify(couponRepository).redeemCoupon("SUMMER10");
        verify(couponRepository).findByCode("SUMMER10");
    }

    @Test
    void reserveCoupon_should_throw_when_exhausted() {

        when(couponRepository.redeemCoupon("SUMMER10"))
                .thenReturn(0);

        UserFriendlyException ex = assertThrows(
                UserFriendlyException.class,
                () -> couponService.reserveCoupon("SUMMER10")
        );

        assertEquals(ErrorCode.COUPON_EXHAUSTED, ex.getErrorCode());

        verify(couponRepository).redeemCoupon("SUMMER10");
        verify(couponRepository, never()).findByCode(any());
    }
}
