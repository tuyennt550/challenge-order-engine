package com.price.orderengine.promotion;

import com.price.orderengine.dto.AppliedPromotionDTO;
import com.price.orderengine.dto.CalculateOrderRequest;
import com.price.orderengine.enums.PromotionType;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PromotionEngineTest {
    @Test
    void should_execute_all_strategies_in_enum_order() {

        PromotionStrategy coupon = mock(PromotionStrategy.class);
        PromotionStrategy vip = mock(PromotionStrategy.class);
        PromotionStrategy buyX = mock(PromotionStrategy.class);
        PromotionStrategy percentage = mock(PromotionStrategy.class);

        when(coupon.getType()).thenReturn(PromotionType.COUPON);
        when(vip.getType()).thenReturn(PromotionType.VIP_DISCOUNT);
        when(buyX.getType()).thenReturn(PromotionType.BUY_2_GET_1_FREE);
        when(percentage.getType()).thenReturn(PromotionType.PERCENTAGE_DISCOUNT);

        PromotionResult empty = emptyResult();

        when(coupon.apply(any())).thenReturn(empty);
        when(vip.apply(any())).thenReturn(empty);
        when(buyX.apply(any())).thenReturn(empty);
        when(percentage.apply(any())).thenReturn(empty);

        PromotionEngine engine =
                new PromotionEngine(List.of(coupon, vip, buyX, percentage));

        PromotionContext context = createContext();

        engine.execute(context);

        InOrder inOrder = inOrder(
                percentage,
                vip,
                coupon,
                buyX
        );

        inOrder.verify(percentage).apply(context);
        inOrder.verify(vip).apply(context);
        inOrder.verify(coupon).apply(context);
        inOrder.verify(buyX).apply(context);
    }

    @Test
    void should_execute_each_strategy_once() {

        PromotionStrategy percentage = mock(PromotionStrategy.class);
        PromotionStrategy coupon = mock(PromotionStrategy.class);

        when(percentage.getType())
                .thenReturn(PromotionType.PERCENTAGE_DISCOUNT);

        when(coupon.getType())
                .thenReturn(PromotionType.COUPON);

        when(percentage.apply(any())).thenReturn(emptyResult());
        when(coupon.apply(any())).thenReturn(emptyResult());

        PromotionEngine engine =
                new PromotionEngine(List.of(percentage, coupon));

        PromotionContext context = createContext();

        engine.execute(context);

        verify(percentage, times(1)).apply(context);
        verify(coupon, times(1)).apply(context);
    }

    @Test
    void should_aggregate_discounts_and_promotions() {

        PromotionStrategy percentage = mock(PromotionStrategy.class);
        PromotionStrategy coupon = mock(PromotionStrategy.class);

        when(percentage.getType())
                .thenReturn(PromotionType.PERCENTAGE_DISCOUNT);

        when(coupon.getType())
                .thenReturn(PromotionType.COUPON);

        when(percentage.apply(any()))
                .thenReturn(
                        PromotionResult.builder()
                                .discount(new BigDecimal("10"))
                                .appliedPromotions(
                                        List.of(new AppliedPromotionDTO(
                                                "PERCENTAGE_DISCOUNT",
                                                new BigDecimal("10")
                                        ))
                                )
                                .build()
                );

        when(coupon.apply(any()))
                .thenReturn(
                        PromotionResult.builder()
                                .discount(new BigDecimal("5"))
                                .appliedPromotions(
                                        List.of(new AppliedPromotionDTO(
                                                "COUPON",
                                                new BigDecimal("5")
                                        ))
                                )
                                .build()
                );

        PromotionEngine engine =
                new PromotionEngine(List.of(coupon, percentage));

        PromotionResult result =
                engine.execute(createContext());

        assertEquals(
                0,
                new BigDecimal("15").compareTo(result.getDiscount())
        );

        assertEquals(
                2,
                result.getAppliedPromotions().size()
        );
    }

    @Test
    void should_return_empty_result_when_no_strategy() {

        PromotionEngine engine =
                new PromotionEngine(List.of());

        PromotionResult result =
                engine.execute(createContext());

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(result.getDiscount())
        );

        assertEquals(
                0,
                result.getAppliedPromotions().size()
        );
    }

    private PromotionResult emptyResult() {
        return PromotionResult.builder()
                .discount(BigDecimal.ZERO)
                .appliedPromotions(List.of())
                .build();
    }

    private PromotionContext createContext() {
        return PromotionContext.builder()
                .request(CalculateOrderRequest.builder().build())
                .promotions(List.of())
                .subtotal(BigDecimal.TEN)
                .build();
    }
}
