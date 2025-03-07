package com.luanvan.orderservice.query.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.luanvan.orderservice.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderResponseModel {
    private String id;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private String userId;
    private Collection<OrderDetail> orderDetails;
    private OrderStatus orderStatus;
    private Delivery delivery;
    private Payment payment;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderStatus{
        private String codeName;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment{
        private String id;
        private String transactionId;
        private String paymentMethod;
        private BigDecimal totalAmount;
        private PaymentStatus status;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
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
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetail{
        private String id;
        private Integer quantity;
        private BigDecimal originalPrice;
        private BigDecimal purchasePrice;
        private Product product;
        private Color color;
        private Size size;
        private Boolean isReview;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        private String id;
        private String name;
        private String description;
        private String images;
        private Boolean isActive;
        private Category category;
        private Collection<ProductColor> productColors;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createdAt;
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updatedAt;
    }


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Category {
        private String id;
        private String name;
        private String codeName;
        private String images;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductColor {
        private String id;
        private BigDecimal price;
        private BigDecimal finalPrice;
        private Boolean isActive;
        private Color color;
        private Promotion promotion;
        private Collection<ProductVariant> productVariants;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Color {
        private String id;
        private String name;
        private String codeName;
        private String colorCode;
        private String description;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Promotion {
        private String id;
        private String name;
        private String codeName;
        private Double discountPercentage;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductVariant {
        private String id;
        private Size size;
        private Integer stock;
        private Integer sold;
        private Boolean isActive;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Size {
        private String id;
        private String name;
        private String codeName;
        private Boolean isActive;
    }

}
