package com.luanvan.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "cart_details")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetail {
    @Id
    private String id;

    @Builder.Default
    @Column(name = "quantity", columnDefinition = "INTEGER DEFAULT 1", nullable = false)
    private Integer quantity = 1;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "cart_id", referencedColumnName = "id", nullable = false)
    private Cart cart;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "color_id", nullable = false)
    private String colorId;

    @Column(name = "size_id", nullable = false)
    private String sizeId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
