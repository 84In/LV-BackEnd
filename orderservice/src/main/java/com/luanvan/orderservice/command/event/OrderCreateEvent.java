package com.luanvan.orderservice.command.event;

import com.luanvan.commonservice.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateEvent {
    private String id;
    private String username;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String paymentMethod;
    private Payment payment;
    private Delivery delivery;
    private List<OrderDetail> orderDetails;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment{
        private String id;
        private BigDecimal totalAmount;
        private PaymentStatus status;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delivery{
        private String id;
        private String recipientName;
        private String phone;
        private String province;
        private String district;
        private String ward;
        private String street;
        private String address;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetail{
        private String id;
        private Long quantity;
        private BigDecimal originalPrice;
        private BigDecimal purchasePrice;
        private String productId;
        private String colorId;
        private String sizeId;
        private String promotionId;
        private Boolean isReview;
    }
}
