package com.luanvan.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "orders" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private String id;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Builder.Default
    @Column(name = "discount_price")
    private BigDecimal discountPrice = BigDecimal.ZERO;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Collection<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "order_status_id", referencedColumnName = "code_name", nullable = false)
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id", referencedColumnName = "id", nullable = false)
    private Delivery delivery;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
