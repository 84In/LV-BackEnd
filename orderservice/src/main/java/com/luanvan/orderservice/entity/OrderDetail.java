package com.luanvan.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    private String id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Builder.Default
    @Column(name = "is_review", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isReview = false;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "color_id", nullable = false)
    private String colorId;

    @Column(name = "size_id", nullable = false)
    private String sizeId;

    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Order order;

    @OneToOne(mappedBy = "orderDetail")
    private Review review;

}
