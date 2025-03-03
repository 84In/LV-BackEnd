package com.luanvan.orderservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateModel {
    private String username;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String paymentMethod;
    private Delivery delivery;
    private List<OrderDetail> orderDetails;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Delivery{
        private String recipientName;
        private String phone;
        private String province;
        private String district;
        private String ward;
        private String street;
        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetail{
        private Integer quantity;
        private BigDecimal originalPrice;
        private BigDecimal purchasePrice;
        private String productId;
        private String colorId;
        private String sizeId;
    }
}
